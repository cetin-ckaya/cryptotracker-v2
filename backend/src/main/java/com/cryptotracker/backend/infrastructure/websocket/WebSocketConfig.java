package com.cryptotracker.backend.infrastructure.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// @EnableWebSocketMessageBroker: STOMP protokolunu aktif eder
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // /topic ile baslayan kanallara mesaj yayinlamak icin in-memory broker kullan
        config.enableSimpleBroker("/topic");

        // Controller'lardaki @MessageMapping metodlari /app prefiksi ile eslenir
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Frontend ve Postman buraya baglanir: ws://localhost:8081/ws
        // SockJS: WebSocket desteklemeyen tarayicilar icin HTTP fallback saglar
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173", "http://localhost:3000")
                .withSockJS();
    }
}
