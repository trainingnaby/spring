package com.formation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /topic : diffusion a tous les clients abonnés.
        // /queue : messages point-a-point, notamment via /user/queue/...
        registry.enableSimpleBroker("/topic", "/queue");

        // /app : messages envoyés du navigateur vers un @MessageMapping.
        registry.setApplicationDestinationPrefixes("/app");

        // /user : préfixe standard pour les messages ciblés vers un utilisateur connecté.
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-duplicatas")
                .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .withSockJS();
    }
}
