package com.cryptotracker.backend.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class PriceMessageConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    public PriceMessageConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Kuyrukta mesaj gelince bu metot otomatik tetiklenir
    @RabbitListener(queues = RabbitMQConfig.PRICE_QUEUE)
    public void consume(PriceUpdateMessage message) {
        // Gelen mesaji WebSocket kanali uzerinden tum istemcilere yayinla
        messagingTemplate.convertAndSend("/topic/prices", message);
    }
}