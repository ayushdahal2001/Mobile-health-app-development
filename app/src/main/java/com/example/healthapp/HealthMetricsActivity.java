package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HealthMetricsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_metrics);

        // Initialize views
        Button btnViewData = findViewById(R.id.btnViewData);
        Button btnSyncDevice = findViewById(R.id.btnSyncDevice);
        Button btnExport = findViewById(R.id.btnExport);
        Button btnSyncDeviceData = findViewById(R.id.btnSyncDeviceData);
        Button btnConnectNewDevice = findViewById(R.id.btnConnectNewDevice);

        // Navigation buttons
        Button btnHome = findViewById(R.id.btnHome);
        Button btnHealthMetrics = findViewById(R.id.btnHealthMetrics);
        Button btnNotification = findViewById(R.id.btnNotification);
        Button btnSettings = findViewById(R.id.btnSettings);

        // Set click listeners
        btnViewData.setOnClickListener(v -> showToast("View Data clicked"));
        btnSyncDevice.setOnClickListener(v -> showToast("Sync Device clicked"));
        btnExport.setOnClickListener(v -> showToast("Export clicked"));
        btnSyncDeviceData.setOnClickListener(v -> showToast("Syncing device data..."));
        btnConnectNewDevice.setOnClickListener(v -> showToast("Connect new device clicked"));

        // Navigation click listeners
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(HealthMetricsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnHealthMetrics.setOnClickListener(v -> showToast("Already on Health Metrics"));

        btnNotification.setOnClickListener(v -> {
            // Start NotificationActivity when implemented
            showToast("Notifications will open here");
        });

        btnSettings.setOnClickListener(v -> {
            // Start SettingsActivity when implemented
            showToast("Settings will open here");
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}