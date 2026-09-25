package com.formation.websocket.realtime;

import java.security.Principal;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final SessionRegistryService sessionRegistryService;
    private final WebSocketMonitoringService monitoringService;
    private final RealtimeNotificationService notificationService;

    public WebSocketEventListener(SessionRegistryService sessionRegistryService,
            WebSocketMonitoringService monitoringService,
            RealtimeNotificationService notificationService) {
        this.sessionRegistryService = sessionRegistryService;
        this.monitoringService = monitoringService;
        this.notificationService = notificationService;
    }

    @EventListener
    public void onConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        if (sessionId == null || sessionId.isBlank()) {
            return;
        }
        String username = resolveUsername(accessor.getUser());

        sessionRegistryService.register(sessionId, username);
        monitoringService.incrementConnections();

        notificationService.broadcastSystemMessage(SystemMessage.of(
                "CONNECTION",
                username + " est connecté au tableau de bord temps réel.",
                username));
        notificationService.broadcastStats();
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        SessionInfo removed = sessionRegistryService.unregister(event.getSessionId());
        String username = removed != null ? removed.getUsername() : "anonymous";

        monitoringService.incrementDisconnections();
        notificationService.broadcastSystemMessage(SystemMessage.of(
                "DISCONNECTION",
                username + " est déconnecté du tableau de bord temps réel.",
                username));
        notificationService.broadcastStats();
    }

    private String resolveUsername(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            return "anonymous";
        }
        return principal.getName();
    }
}
