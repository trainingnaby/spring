package com.formation.websocket.realtime;

public class DashboardStats {
    private int connectedUsers;
    private long totalConnections;
    private long totalDisconnections;
    private long messagesSent;

    public DashboardStats() {
    }

    public DashboardStats(int connectedUsers, long totalConnections, long totalDisconnections, long messagesSent) {
        this.connectedUsers = connectedUsers;
        this.totalConnections = totalConnections;
        this.totalDisconnections = totalDisconnections;
        this.messagesSent = messagesSent;
    }

    public int getConnectedUsers() { return connectedUsers; }
    public void setConnectedUsers(int connectedUsers) { this.connectedUsers = connectedUsers; }
    public long getTotalConnections() { return totalConnections; }
    public void setTotalConnections(long totalConnections) { this.totalConnections = totalConnections; }
    public long getTotalDisconnections() { return totalDisconnections; }
    public void setTotalDisconnections(long totalDisconnections) { this.totalDisconnections = totalDisconnections; }
    public long getMessagesSent() { return messagesSent; }
    public void setMessagesSent(long messagesSent) { this.messagesSent = messagesSent; }
}
