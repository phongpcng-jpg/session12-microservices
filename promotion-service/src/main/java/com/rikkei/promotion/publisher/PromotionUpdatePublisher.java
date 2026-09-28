package com.rikkei.promotion.publisher;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.rikkei.common.constant.Channels;
import com.rikkei.promotion.service.PromotionChangedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionUpdatePublisher {

    private final StringRedisTemplate redisTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(PromotionChangedEvent event) {
        Long receivers = redisTemplate.convertAndSend(Channels.PROMOTION_UPDATES, String.valueOf(event.productId()));
        log.info("Published promotion update for product {} to {} subscriber(s)", event.productId(), receivers);
    }
}
