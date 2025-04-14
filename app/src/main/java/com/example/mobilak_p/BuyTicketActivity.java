package com.example.mobilak_p;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyTicketActivity extends AppCompatActivity {

    private class Ticket {
        String name;
        int price;
        int quantity = 0;

        Ticket(String name, int price) {
            this.name = name;
            this.price = price;
        }
    }

    private LinearLayout ticketListLayout;
    private Button buyButton;
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private ArrayList<TextView> quantityTexts = new ArrayList<>();
    private int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_ticket);

        ticketListLayout = findViewById(R.id.ticketList);
        buyButton = findViewById(R.id.buttonBuy);

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());


        tickets.add(new Ticket("Budapest → London", 25000));
        tickets.add(new Ticket("Budapest → Párizs", 28000));
        tickets.add(new Ticket("Budapest → Róma", 22000));
        tickets.add(new Ticket("Budapest → Berlin", 21000));
        tickets.add(new Ticket("Budapest → Amszterdam", 27000));
        tickets.add(new Ticket("Budapest → Bécs", 15000));
        tickets.add(new Ticket("Budapest → Prága", 19000));
        tickets.add(new Ticket("Budapest → Madrid", 30000));
        tickets.add(new Ticket("Budapest → Barcelona", 29000));
        tickets.add(new Ticket("Budapest → Koppenhága", 26000));

        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            addTicketView(ticket, i);
        }

        buyButton.setOnClickListener(v -> {

        });

        updateTotalPrice();
    }

    private void addTicketView(Ticket ticket, int index) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, 16, 0, 16);

        TextView title = new TextView(this);
        title.setText(ticket.name + " - " + ticket.price + " Ft");
        title.setTextSize(16f);
        row.addView(title);

        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER_VERTICAL);

        Button minus = new Button(this);
        minus.setText("-");
        minus.setOnClickListener(v -> {
            if (ticket.quantity > 0) {
                ticket.quantity--;
                quantityTexts.get(index).setText(String.valueOf(ticket.quantity));
                updateTotalPrice();
            }
        });

        TextView quantity = new TextView(this);
        quantity.setText("0");
        quantity.setPadding(24, 0, 24, 0);
        quantity.setTextSize(18f);

        Button plus = new Button(this);
        plus.setText("+");
        plus.setOnClickListener(v -> {
            ticket.quantity++;
            quantity.setText(String.valueOf(ticket.quantity));
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
            totalPrice += t.quantity * t.price;
        }
        buyButton.setText("Vásárlás - " + totalPrice + " Ft");
    }

}
