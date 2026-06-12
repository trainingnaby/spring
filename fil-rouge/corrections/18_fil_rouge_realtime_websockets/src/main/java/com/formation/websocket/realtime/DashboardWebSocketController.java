package com.formation.websocket.realtime;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DashboardWebSocketController {

    private final WebSocketMonitoringService monitoringService;
    private final RealtimeNotificationService notificationService;

    public DashboardWebSocketController(WebSocketMonitoringService monitoringService,
            RealtimeNotificationService notificationService) {
        this.monitoringService = monitoringService;
        this.notificationService = notificationService;
    }

    @MessageMapping("/dashboard/ping")
    @SendTo("/topic/system")
    public SystemMessage ping(Principal principal) {
        String username = principal != null ? principal.getName() : "anonymous";
        notificationService.broadcastStats();
        return SystemMessage.of("PING", username + " a envoyé un ping depuis le dashboard.", username);
    }

    @MessageMapping("/dashboard/stats")
    @SendTo("/topic/statistiques")
    public DashboardStats stats() {
        return monitoringService.currentStats();
    }
}
