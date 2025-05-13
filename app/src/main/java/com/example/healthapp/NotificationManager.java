package com.example.healthapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationManager {
    private static final int HIGH_HEART_RATE_THRESHOLD = 100;
    private static final int LOW_SLEEP_THRESHOLD = 6; // hours
    private static final int STEP_GOAL = 10000;

    public static List<HealthNotification> generateNotifications(
            int heartRate, int sleepHours, int steps) {

        List<HealthNotification> notifications = new ArrayList<>();

        // Heart rate alert
        if (heartRate > HIGH_HEART_RATE_THRESHOLD) {
            notifications.add(new HealthNotification(
                    "High Heart Rate Alert",
                    "Your heart rate was " + heartRate + " bpm (above normal threshold)",
                    getCurrentTime(),
                    HealthNotification.NotificationType.HEART_RATE
            ));
        }

        // Sleep alert
        if (sleepHours < LOW_SLEEP_THRESHOLD) {
            notifications.add(new HealthNotification(
                    "Low Sleep Alert",
                    "You only slept " + sleepHours + " hours last night",
                    getCurrentTime(),
                    HealthNotification.NotificationType.SLEEP
            ));
        }

        // Step goal
        if (steps >= STEP_GOAL) {
            notifications.add(new HealthNotification(
                    "Step Goal Achieved!",
                    "You've reached your daily goal of " + STEP_GOAL + " steps",
                    getCurrentTime(),
                    HealthNotification.NotificationType.STEPS
            ));
        }

        return notifications;
    }

    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }
}