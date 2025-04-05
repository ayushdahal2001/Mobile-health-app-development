package com.example.healthapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends BaseActivity {


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
//        Button btnHealthMetricsNav = findViewById(R.id.btnHealthMetricsNav);
        Button btnNotification = findViewById(R.id.btnNotification);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnLogin = findViewById(R.id.btnLogin);




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
        // healthmerticintent
        View.OnClickListener buttonClickListener1 = new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HealthMetricsActivity.class);
                startActivity(intent);

            }
        };
        btnHealthMetrics.setOnClickListener(buttonClickListener1);

        //notification intent
        View.OnClickListener buttonClickListener2 = new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
                startActivity(intent);

            }
        };
        btnNotification.setOnClickListener(buttonClickListener2);

        View.OnClickListener buttonClickListener3 = new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);

            }
        };
        btnSettings.setOnClickListener(buttonClickListener3);

        View.OnClickListener buttonClickListener4 = new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);

            }
        };
        btnLogin.setOnClickListener(buttonClickListener4);
    }
}