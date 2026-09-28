package com.rikkei.promotion.dto;

import java.math.BigDecimal;

public record PromotionResponse(Long productId, BigDecimal promotionPrice, boolean active) {
}
