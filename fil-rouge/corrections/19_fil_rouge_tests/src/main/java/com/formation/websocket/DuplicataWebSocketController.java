package com.formation.websocket;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DuplicataWebSocketController {

    /**
     * Endpoint pédagogique optionnel.
     *
     * Un client STOMP peut envoyer un message vers /app/duplicatas/ping.
     * La réponse sera diffusée à tous les abonnés de /topic/duplicatas/debug.
     */
    @MessageMapping("/duplicatas/ping")
    @SendTo("/topic/duplicatas/debug")
    public Map<String, Object> ping(Map<String, Object> message) {
        return Map.of(
                "message", "Ping reçu côté serveur",
                "payload", message,
                "date", LocalDateTime.now().toString());
    }
}
