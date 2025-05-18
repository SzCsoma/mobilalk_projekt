package com.example.mobilak_p;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {

    private Button confirmLogoutBtn, cancelLogoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        confirmLogoutBtn = findViewById(R.id.buttonConfirmLogout);
        cancelLogoutBtn = findViewById(R.id.buttonCancelLogout);

        confirmLogoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(LogoutActivity.this, "Sikeres kijelentkezés", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        cancelLogoutBtn.setOnClickListener(v -> finish());
    }
}
