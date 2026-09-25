package com.formation.websocket.realtime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RealtimeNotificationService {

    public static final String TOPIC_SYSTEM = "/topic/system";
    public static final String TOPIC_STATS = "/topic/statistiques";
    public static final String USER_QUEUE_NOTIFICATIONS = "/queue/notifications";

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketMonitoringService monitoringService;

    public RealtimeNotificationService(SimpMessagingTemplate messagingTemplate,
            WebSocketMonitoringService monitoringService) {
        this.messagingTemplate = messagingTemplate;
        this.monitoringService = monitoringService;
    }

    public void broadcastSystemMessage(SystemMessage message) {
        messagingTemplate.convertAndSend(TOPIC_SYSTEM, message);
        monitoringService.incrementMessagesSent();
    }

    public void broadcastStats() {
        messagingTemplate.convertAndSend(TOPIC_STATS, monitoringService.currentStats());
        monitoringService.incrementMessagesSent();
    }

    public void sendToUser(String username, UserNotification notification) {
        if (username == null || username.isBlank() || "anonymous".equals(username)) {
            return;
        }
        messagingTemplate.convertAndSendToUser(username, USER_QUEUE_NOTIFICATIONS, notification);
        monitoringService.incrementMessagesSent();
    }
}
