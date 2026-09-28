package com.rikkei.product.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String imageUrl,
        BigDecimal price,
        BigDecimal promotionPrice,
        BigDecimal effectivePrice,
        Instant updatedAt) {
}
