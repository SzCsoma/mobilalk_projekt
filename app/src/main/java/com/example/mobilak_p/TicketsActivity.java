package com.example.mobilak_p;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class TicketsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private LinearLayout ticketListLayout;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;

    // Barcode olvasó launcher
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String qrData = result.getContents();
                    Toast.makeText(this, "Beolvasott QR: " + qrData, Toast.LENGTH_LONG).show();
                    navigateBasedOnQr(qrData);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        ticketListLayout = findViewById(R.id.ticketListLayout);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadPurchasedTickets();


        Button scanQrButton = findViewById(R.id.scanQrButton);
        scanQrButton.setOnClickListener(v -> checkCameraPermissionAndStartScanner());
    }

    private void loadPurchasedTickets() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("purchased_tickets")
                .document(userId)
                .collection("tickets")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ticketListLayout.removeAllViews(); // korábbi nézetek törlése
                    if (queryDocumentSnapshots.isEmpty()) {
                        TextView empty = new TextView(this);
                        empty.setText("Nincs megvásárolt jegyed.");
                        ticketListLayout.addView(empty);
                    } else {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String name = doc.getString("name");
                            Long price = doc.getLong("price");
                            Long quantity = doc.getLong("quantity");

                            LinearLayout ticketItemLayout = new LinearLayout(this);
                            ticketItemLayout.setOrientation(LinearLayout.VERTICAL);
                            ticketItemLayout.setPadding(0, 30, 0, 30);

                            TextView ticketView = new TextView(this);
                            ticketView.setText(name + " (" + quantity + " db) - " + (price * quantity) + " Ft");
                            ticketView.setTextSize(16f);

                            Button updateButton = new Button(this);
                            updateButton.setText("Mennyiség növelése");
                            updateButton.setOnClickListener(v -> {
                                Long newQuantity = quantity + 1;
                                db.collection("purchased_tickets")
                                        .document(userId)
                                        .collection("tickets")
                                        .document(doc.getId())
                                        .update("quantity", newQuantity)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Mennyiség frissítve", Toast.LENGTH_SHORT).show();
                                            loadPurchasedTickets(); // frissítés után újratöltés
                                        });
                            });

                            Button deleteButton = new Button(this);
                            deleteButton.setText("Jegy törlése");
                            deleteButton.setOnClickListener(v -> {
                                db.collection("purchased_tickets")
                                        .document(userId)
                                        .collection("tickets")
                                        .document(doc.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Jegy törölve", Toast.LENGTH_SHORT).show();
                                            loadPurchasedTickets(); // újratöltés törlés után
                                        });
                            });

                            ticketItemLayout.addView(ticketView);
                            ticketItemLayout.addView(updateButton);
                            ticketItemLayout.addView(deleteButton);

                            ticketListLayout.addView(ticketItemLayout);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Hiba a jegyek lekérésekor", e));
    }


    private void checkCameraPermissionAndStartScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startQrScanner();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQrScanner();
            } else {
                Toast.makeText(this, "Kamera engedély szükséges a QR-kód beolvasáshoz", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startQrScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("QR kód beolvasása");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        barcodeLauncher.launch(options);
    }

    private void navigateBasedOnQr(String qrData) {
        // Itt navigálj másik Activity-be a qrData-val, pl:
        Intent intent = new Intent(this, TicketsActivity.class);
        intent.putExtra("QR_DATA", qrData);
        startActivity(intent);
    }
}
