package com.formation.websocket.realtime;

import java.time.LocalDateTime;

public class SessionInfo {
    private String sessionId;
    private String username;
    private LocalDateTime connectedAt;

    public SessionInfo() {
    }

    public SessionInfo(String sessionId, String username, LocalDateTime connectedAt) {
        this.sessionId = sessionId;
        this.username = username;
        this.connectedAt = connectedAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getConnectedAt() {
        return connectedAt;
    }

    public void setConnectedAt(LocalDateTime connectedAt) {
        this.connectedAt = connectedAt;
    }
}
