package com.formation.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.formation.domain.Duplicata;
import com.formation.websocket.realtime.RealtimeNotificationService;
import com.formation.websocket.realtime.SystemMessage;
import com.formation.websocket.realtime.UserNotification;
import com.formation.websocket.realtime.WebSocketMonitoringService;

@Component
public class DuplicataWebSocketNotifier {

    public static final String TOPIC_DUPLICATAS = "/topic/duplicatas";

    private final SimpMessagingTemplate messagingTemplate;
    private final RealtimeNotificationService realtimeNotificationService;
    private final WebSocketMonitoringService monitoringService;

    public DuplicataWebSocketNotifier(SimpMessagingTemplate messagingTemplate,
            RealtimeNotificationService realtimeNotificationService,
            WebSocketMonitoringService monitoringService) {
        this.messagingTemplate = messagingTemplate;
        this.realtimeNotificationService = realtimeNotificationService;
        this.monitoringService = monitoringService;
    }

    public void notifierCreation(Duplicata duplicata) {
        DuplicataNotification notification = DuplicataNotification.creation(duplicata);
        messagingTemplate.convertAndSend(TOPIC_DUPLICATAS, notification);
        monitoringService.incrementMessagesSent();

        realtimeNotificationService.broadcastSystemMessage(SystemMessage.of(
                "DUPLICATA_CREATED",
                "Nouveau duplicata " + duplicata.getId() + " créé pour " + duplicata.getUserId()
                        + " pour un montant de " + duplicata.getMontant() + " euros.",
                duplicata.getUserId()));
        realtimeNotificationService.sendToUser(duplicata.getUserId(), UserNotification.of(
                "Demande enregistrée",
                "Votre demande de duplicata " + duplicata.getId() + " a été enregistrée."));
        realtimeNotificationService.broadcastStats();
    }

    public void notifierSuppression(Duplicata duplicata) {
        DuplicataNotification notification = DuplicataNotification.suppression(duplicata);
        messagingTemplate.convertAndSend(TOPIC_DUPLICATAS, notification);
        monitoringService.incrementMessagesSent();

        realtimeNotificationService.broadcastSystemMessage(SystemMessage.of(
                "DUPLICATA_DELETED",
                "Duplicata " + duplicata.getId() + " supprimé pour " + duplicata.getUserId() + ".",
                duplicata.getUserId()));
        realtimeNotificationService.sendToUser(duplicata.getUserId(), UserNotification.of(
                "Demande supprimée",
                "Votre duplicata " + duplicata.getId() + " a été supprimé."));
        realtimeNotificationService.broadcastStats();
    }
}
