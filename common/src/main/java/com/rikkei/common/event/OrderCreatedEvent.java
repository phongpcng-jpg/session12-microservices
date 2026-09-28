package com.rikkei.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        Long orderId,
        UUID reservationId,
        Long productId,
        int quantity,
        String customerEmail,
        BigDecimal totalAmount,
        Instant createdAt) {
}
