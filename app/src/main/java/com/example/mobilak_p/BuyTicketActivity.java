package com.example.mobilak_p;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BuyTicketActivity extends AppCompatActivity {

    private LinearLayout ticketListLayout;
    private Button buyButton;
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private ArrayList<TextView> quantityTexts = new ArrayList<>();
    private int totalPrice = 0;

    private FirebaseFirestore db;

    private Button buttonSortName, buttonSortPriceAsc, buttonSortPriceDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_ticket);

        ticketListLayout = findViewById(R.id.ticketList);
        buyButton = findViewById(R.id.buttonBuy);
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        // Szűrőgombok inicializálása
        buttonSortName = findViewById(R.id.buttonSortName);
        buttonSortPriceAsc = findViewById(R.id.buttonSortPriceAsc);
        buttonSortPriceDesc = findViewById(R.id.buttonSortPriceDesc);

        buttonSortName.setOnClickListener(v -> {
            Collections.sort(tickets, Comparator.comparing(Ticket::getName));
            refreshTicketList();
        });

        buttonSortPriceAsc.setOnClickListener(v -> {
            Collections.sort(tickets, Comparator.comparingInt(Ticket::getPrice));
            refreshTicketList();
        });

        buttonSortPriceDesc.setOnClickListener(v -> {
            Collections.sort(tickets, (a, b) -> Integer.compare(b.getPrice(), a.getPrice()));
            refreshTicketList();
        });

        db = FirebaseFirestore.getInstance();
        loadTicketsFromFirestore();

        buyButton.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                String userId = user.getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                for (Ticket ticket : tickets) {
                    if (ticket.getQuantity() > 0) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", ticket.getName());
                        data.put("price", ticket.getPrice());
                        data.put("quantity", ticket.getQuantity());
                        data.put("timestamp", FieldValue.serverTimestamp());

                        db.collection("purchased_tickets")
                                .document(userId)
                                .collection("tickets")
                                .add(data);
                    }
                }

                NotificationHelper.createNotificationChannel(this);
                NotificationHelper.showNotification(
                        this,
                        "Sikeres vásárlás",
                        "Köszönjük a vásárlásod!",
                        1001
                );

                Toast.makeText(this, "Vásárlás sikeres!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Nem vagy bejelentkezve.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTicketsFromFirestore() {
        db.collection("available_tickets")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    tickets.clear();
                    quantityTexts.clear();
                    ticketListLayout.removeAllViews();
                    int index = 0;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Ticket ticket = doc.toObject(Ticket.class);
                        ticket.setId(doc.getId());
                        // Visszatöltéskor korábban elmentett mennyiség beállítása
                        int qty = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                .getInt("ticket_qty_" + ticket.getId(), 0);
                        ticket.setQuantity(qty);

                        tickets.add(ticket);
                        addTicketView(ticket, index);
                        index++;
                    }
                    updateTotalPrice();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Hiba a jegyek lekérésekor", e));
    }

    private void refreshTicketList() {
        ticketListLayout.removeAllViews();
        quantityTexts.clear();
        for (int i = 0; i < tickets.size(); i++) {
            addTicketView(tickets.get(i), i);
        }
        updateTotalPrice();
    }

    private void addTicketView(Ticket ticket, int index) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, 16, 0, 16);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        row.startAnimation(fadeIn);

        TextView title = new TextView(this);
        title.setText(ticket.getName() + " - " + ticket.getPrice() + " Ft");
        title.setTextSize(16f);
        row.addView(title);

        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER_VERTICAL);

        Button minus = new Button(this);
        minus.setText("-");
        minus.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(this, R.anim.button_scale);
            v.startAnimation(scale);

            if (ticket.getQuantity() > 0) {
                ticket.setQuantity(ticket.getQuantity() - 1);
                quantityTexts.get(index).setText(String.valueOf(ticket.getQuantity()));
                updateTotalPrice();
            }
        });

        TextView quantity = new TextView(this);
        quantity.setText(String.valueOf(ticket.getQuantity()));
        quantity.setPadding(24, 0, 24, 0);
        quantity.setTextSize(18f);

        Button plus = new Button(this);
        plus.setText("+");
        plus.setOnClickListener(v -> {
            Animation scale = AnimationUtils.loadAnimation(this, R.anim.button_scale);
            v.startAnimation(scale);

            ticket.setQuantity(ticket.getQuantity() + 1);
            quantity.setText(String.valueOf(ticket.getQuantity()));
            updateTotalPrice();
        });

        controls.addView(minus);
        controls.addView(quantity);
        controls.addView(plus);

        row.addView(controls);
        ticketListLayout.addView(row);

        quantityTexts.add(quantity);
    }

    private void updateTotalPrice() {
        totalPrice = 0;
        for (Ticket t : tickets) {
            totalPrice += t.getQuantity() * t.getPrice();
        }
        buyButton.setText("Vásárlás - " + totalPrice + " Ft");
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCurrentTicketSelection();
    }

    private void saveCurrentTicketSelection() {
        var prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        var editor = prefs.edit();

        for (Ticket ticket : tickets) {
            editor.putInt("ticket_qty_" + ticket.getId(), ticket.getQuantity());
        }
        editor.apply();
    }
}
