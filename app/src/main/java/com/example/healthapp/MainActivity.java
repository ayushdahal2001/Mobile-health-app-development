package com.example.healthapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons and set click listeners
        Button btnSyncDevice = findViewById(R.id.btnSyncDevice);
        Button btnExportData = findViewById(R.id.btnExportData);
        Button btnManageAccount = findViewById(R.id.btnManageAccount);
        Button btnHealthMetrics = findViewById(R.id.btnHealthMetrics);
        Button btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        Button btnViewHealthData = findViewById(R.id.btnViewHealthData);
        Button btnHome = findViewById(R.id.btnHome);
        Button btnHealthMetricsNav = findViewById(R.id.btnHealthMetricsNav);
        Button btnNotification = findViewById(R.id.btnNotification);
        Button btnSettings = findViewById(R.id.btnSettings);

        // Set click listeners for all buttons
        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText = ((Button) v).getText().toString();
                Toast.makeText(MainActivity.this, buttonText + " clicked", Toast.LENGTH_SHORT).show();
                // Here you would typically start a new activity or perform some action
            }
        };


        btnSyncDevice.setOnClickListener(buttonClickListener);
        btnExportData.setOnClickListener(buttonClickListener);
        btnManageAccount.setOnClickListener(buttonClickListener);
//
        btnUpdateProfile.setOnClickListener(buttonClickListener);
        btnViewHealthData.setOnClickListener(buttonClickListener);
        btnHome.setOnClickListener(buttonClickListener);
        btnHealthMetricsNav.setOnClickListener(buttonClickListener);
        btnNotification.setOnClickListener(buttonClickListener);
        btnSettings.setOnClickListener(buttonClickListener);



        // Emergency SOS long press
        View emergencySosArea = findViewById(R.id.emergencySosArea); // You'll need to add this ID to your layout
        emergencySosArea.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Emergency SOS activated!", Toast.LENGTH_LONG).show();
                // Here you would implement actual emergency functionality
                return true;
            }
        });
        View.OnClickListener buttonClickListener1 = new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HealthMetricsActivity.class);
                startActivity(intent);
                finish();
            }
        };
        btnHealthMetrics.setOnClickListener(buttonClickListener1);
    }
    public void onClickBack1(View v) {
        Intent intent1 = new Intent(MainActivity.this, HealthMetricsActivity.class);
        startActivity(intent1);
        finish();
    }
}