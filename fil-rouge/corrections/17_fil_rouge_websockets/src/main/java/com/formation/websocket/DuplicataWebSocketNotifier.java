package com.formation.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.formation.domain.Duplicata;

@Component
public class DuplicataWebSocketNotifier {

    public static final String TOPIC_DUPLICATAS = "/topic/duplicatas";

    private final SimpMessagingTemplate messagingTemplate;

    public DuplicataWebSocketNotifier(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifierCreation(Duplicata duplicata) {
        messagingTemplate.convertAndSend(TOPIC_DUPLICATAS, DuplicataNotification.creation(duplicata));
    }

    public void notifierSuppression(Duplicata duplicata) {
        messagingTemplate.convertAndSend(TOPIC_DUPLICATAS, DuplicataNotification.suppression(duplicata));
    }
}
