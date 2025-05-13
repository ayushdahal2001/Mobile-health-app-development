package com.example.healthapp;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.ImageButton;
import android.window.OnBackInvokedDispatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


//public class NotificationsActivity extends BaseActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_notifications);
//
//
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar back button click
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    // Let Android handle the back navigation automatically
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
//
//
//    }
public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize RecyclerView
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get health data from intent or shared preferences
        int heartRate = getIntent().getIntExtra("HEART_RATE", 0);
        int sleepHours = getIntent().getIntExtra("SLEEP_HOURS", 0);
        int steps = getIntent().getIntExtra("STEPS", 0);

        // Generate notifications based on health data
        List<HealthNotification> notifications =
                NotificationManager.generateNotifications(heartRate, sleepHours, steps);

        // Add system notifications
        notifications.add(new HealthNotification(
                "Device Sync Required",
                "Please sync your device to update data",
                "1 hour ago",
                HealthNotification.NotificationType.SYSTEM
        ));

        // Set up adapter
        adapter = new NotificationAdapter(notifications);
        notificationsRecyclerView.setAdapter(adapter);
    }
}









