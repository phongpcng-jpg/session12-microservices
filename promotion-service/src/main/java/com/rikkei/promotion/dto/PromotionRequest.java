package com.rikkei.promotion.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record PromotionRequest(
        @NotNull @DecimalMin("0.0") BigDecimal promotionPrice,
        boolean active) {
}
