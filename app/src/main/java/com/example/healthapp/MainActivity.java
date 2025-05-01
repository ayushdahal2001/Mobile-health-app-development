package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends BaseActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();


        // Initialize buttons and set click listeners
        Button btnSyncDevice = findViewById(R.id.btnSyncDevice);
        Button btnExportData = findViewById(R.id.btnExportData);
        Button btnManageAccount = findViewById(R.id.btnManageAccount);
        Button btnHealthMetrics = findViewById(R.id.btnHealthMetrics);

        Button btnViewHealthData = findViewById(R.id.btnViewHealthData);
        Button btnHome = findViewById(R.id.btnHome);
        Button btnNotification = findViewById(R.id.btnNotification);
        Button btnSettings = findViewById(R.id.btnSettings);


        // Emergency SOS long press
        View emergencySosArea = findViewById(R.id.emergencySosArea);
        emergencySosArea.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Emergency SOS activated!", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        // Set click listeners for buttons
        btnHealthMetrics.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HealthMetricsActivity.class)));
        btnNotification.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NotificationsActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
        // In your MainActivity's onCreate()
        btnManageAccount = findViewById(R.id.btnManageAccount);
        btnManageAccount.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ManageAccountActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly
        if (auth.getCurrentUser() == null) {
            // No user is signed in, redirect to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // Close MainActivity
        }
    }
}