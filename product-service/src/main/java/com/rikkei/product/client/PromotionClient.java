package com.rikkei.product.client;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "promotion-service")
public interface PromotionClient {

    @GetMapping("/promotions/{productId}")
    PromotionView find(@PathVariable("productId") Long productId);

    record PromotionView(Long productId, BigDecimal promotionPrice, boolean active) {
    }
}
