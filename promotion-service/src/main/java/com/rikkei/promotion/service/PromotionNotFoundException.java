package com.rikkei.promotion.service;

public class PromotionNotFoundException extends RuntimeException {

    public PromotionNotFoundException(Long productId) {
        super("Promotion not found for product: " + productId);
    }
}
