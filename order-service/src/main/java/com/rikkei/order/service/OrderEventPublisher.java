package com.rikkei.order.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.rikkei.common.constant.Topics;
import com.rikkei.common.event.OrderCreatedEvent;
import com.rikkei.order.config.AppProperties;
import com.rikkei.order.exception.DependencyUnavailableException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final AppProperties properties;

    public void publish(OrderCreatedEvent event) {
        try {
            kafkaTemplate.send(Topics.ORDER_EVENTS, String.valueOf(event.orderId()), event)
                    .get(properties.kafka().sendTimeout().toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DependencyUnavailableException("Không thể gửi sự kiện đơn hàng", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new DependencyUnavailableException("Không thể gửi sự kiện đơn hàng", e);
        }
    }
}
