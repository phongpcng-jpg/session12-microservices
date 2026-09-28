package com.rikkei.order.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.rikkei.order.domain.OrderStatus;

public record OrderResponse(
        Long id,
        Long productId,
        int quantity,
        String customerEmail,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt) {
}
