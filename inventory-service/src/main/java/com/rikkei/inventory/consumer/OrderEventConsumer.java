package com.rikkei.inventory.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.rikkei.common.constant.Topics;
import com.rikkei.common.event.OrderCreatedEvent;
import com.rikkei.inventory.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = Topics.ORDER_EVENTS, groupId = Topics.INVENTORY_GROUP)
    public void onOrderCreated(OrderCreatedEvent event) {
        inventoryService.confirm(event.reservationId());
        log.info("Inventory confirmed reservation {} for order {}", event.reservationId(), event.orderId());
    }
}
