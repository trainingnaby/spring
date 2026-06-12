package com.formation.websocket.realtime;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class WebSocketMonitoringService {

    private final AtomicLong totalConnections = new AtomicLong();
    private final AtomicLong totalDisconnections = new AtomicLong();
    private final AtomicLong messagesSent = new AtomicLong();
    private final SessionRegistryService sessionRegistryService;

    public WebSocketMonitoringService(SessionRegistryService sessionRegistryService) {
        this.sessionRegistryService = sessionRegistryService;
    }

    public void incrementConnections() {
        totalConnections.incrementAndGet();
    }

    public void incrementDisconnections() {
        totalDisconnections.incrementAndGet();
    }

    public void incrementMessagesSent() {
        messagesSent.incrementAndGet();
    }

    public DashboardStats currentStats() {
        return new DashboardStats(
                sessionRegistryService.countConnectedUsers(),
                totalConnections.get(),
                totalDisconnections.get(),
                messagesSent.get());
    }
}
