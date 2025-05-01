package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HealthMetricsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_metrics);


        Button btnSyncDeviceData = findViewById(R.id.btnSyncDeviceData);
        Button btnConnectNewDevice = findViewById(R.id.btnConnectNewDevice);


        btnSyncDeviceData.setOnClickListener(v -> showToast("Syncing device data..."));
        btnConnectNewDevice.setOnClickListener(v -> showToast("Connect new device clicked"));

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}