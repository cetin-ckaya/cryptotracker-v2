package com.cryptotracker.backend.infrastructure.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Kuyruk adi sabit tutulur — producer ve consumer ayni adi kullanir
    public static final String PRICE_QUEUE = "price.updates";

    // Spring'e bu isimde bir kuyruk olusturmasini soyle
    // durable: true — RabbitMQ yeniden baslatilsa bile kuyruk silinmez
    @Bean
    public Queue priceQueue() {
        return new Queue(PRICE_QUEUE, true);
    }

    // Mesajlari Java nesnesi yerine JSON olarak gonder/al
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate: mesaj gondermek icin kullanilan ana sinif
    // JSON converter'i inject et — otomatik serialize/deserialize yapar
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}