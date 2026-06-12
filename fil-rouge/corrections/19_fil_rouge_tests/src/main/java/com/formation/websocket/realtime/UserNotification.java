package com.formation.websocket.realtime;

import java.time.LocalDateTime;

public class UserNotification {
    private String title;
    private String message;
    private LocalDateTime timestamp;

    public UserNotification() {
    }

    public UserNotification(String title, String message, LocalDateTime timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public static UserNotification of(String title, String message) {
        return new UserNotification(title, message, LocalDateTime.now());
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
