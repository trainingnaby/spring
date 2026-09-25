package com.formation.websocket.realtime;

import java.time.LocalDateTime;

public class SystemMessage {
    private String type;
    private String message;
    private String username;
    private LocalDateTime timestamp;

    public SystemMessage() {
    }

    public SystemMessage(String type, String message, String username, LocalDateTime timestamp) {
        this.type = type;
        this.message = message;
        this.username = username;
        this.timestamp = timestamp;
    }

    public static SystemMessage of(String type, String message, String username) {
        return new SystemMessage(type, message, username, LocalDateTime.now());
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
