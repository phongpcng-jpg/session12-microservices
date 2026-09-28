package com.rikkei.product.listener;

import java.nio.charset.StandardCharsets;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.rikkei.common.constant.CacheNames;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionUpdateListener implements MessageListener {

    private final CacheManager cacheManager;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8).trim();
        Long productId;
        try {
            productId = Long.valueOf(payload);
        } catch (NumberFormatException e) {
            log.warn("Ignored promotion update with invalid product id: {}", payload);
            return;
        }
        Cache cache = cacheManager.getCache(CacheNames.PRODUCT);
        if (cache != null) {
            cache.evict(productId);
            log.info("Evicted product cache for id {}", productId);
        }
    }
}
