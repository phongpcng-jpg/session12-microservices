package com.rikkei.product.client;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Component;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionGateway {

    private final PromotionClient promotionClient;

    public Optional<BigDecimal> activePrice(Long productId) {
        try {
            PromotionClient.PromotionView view = promotionClient.find(productId);
            if (view != null && view.active()) {
                return Optional.ofNullable(view.promotionPrice());
            }
            return Optional.empty();
        } catch (FeignException.NotFound e) {
            return Optional.empty();
        } catch (RuntimeException e) {
            log.warn("Promotion lookup failed for product {}: {}", productId, e.getMessage());
            return Optional.empty();
        }
    }
}
