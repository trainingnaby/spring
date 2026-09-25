package com.formation.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.formation.websocket.realtime.WebSocketMonitoringService;

@Component("websocket")
public class WebSocketHealthIndicator implements HealthIndicator {

    private final WebSocketMonitoringService monitoringService;

    public WebSocketHealthIndicator(WebSocketMonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @Override
    public Health health() {
        var stats = monitoringService.currentStats();
        return Health.up()
                .withDetail("connectedUsers", stats.getConnectedUsers())
                .withDetail("totalConnections", stats.getTotalConnections())
                .withDetail("totalDisconnections", stats.getTotalDisconnections())
                .withDetail("messagesSent", stats.getMessagesSent())
                .build();
    }
}
