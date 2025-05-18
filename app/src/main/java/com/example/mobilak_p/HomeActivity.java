package com.example.mobilak_p;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button buttonMyTickets, buttonBuyTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Üdvözlünk a legjobb repjegyeket árusító appban!");

        buttonMyTickets = findViewById(R.id.buttonMyTickets);
        buttonBuyTicket = findViewById(R.id.buttonBuyTicket);

        buttonMyTickets.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, TicketsActivity.class)));

        buttonBuyTicket.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, BuyTicketActivity.class)));

        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LogoutActivity.class);
            startActivity(intent);
        });
        Intent intent = new Intent(this, PromoNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        long triggerTime = System.currentTimeMillis() + 10 * 1000;

        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
        );
    }
}
