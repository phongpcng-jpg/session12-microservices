package com.rikkei.email.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.rikkei.common.constant.Topics;
import com.rikkei.common.event.OrderCreatedEvent;
import com.rikkei.email.service.OrderMailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderMailService orderMailService;

    @KafkaListener(topics = Topics.ORDER_EVENTS, groupId = Topics.EMAIL_GROUP)
    public void onOrderCreated(OrderCreatedEvent event) {
        orderMailService.sendOrderConfirmation(event);
        log.info("Sent confirmation email for order {} to {}", event.orderId(), event.customerEmail());
    }
}
