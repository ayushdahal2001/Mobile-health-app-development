package com.example.healthapp;

public class HealthNotification {
    private String title;
    private String message;
    private String time;
    private NotificationType type;

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }
    public NotificationType getType() {
        return type;
    }

    public enum NotificationType {
        HEART_RATE, SLEEP, STEPS, SYSTEM
    }

    // Constructor, getters and setters
    public HealthNotification(String title, String message, String time, NotificationType type) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.type = type;
    }

    // ... getters and setters
}