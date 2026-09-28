package com.rikkei.inventory.service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.rikkei.common.constant.LockKeys;
import com.rikkei.inventory.config.AppProperties;
import com.rikkei.inventory.exception.LockNotAcquiredException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductLockExecutor {

    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;
    private final AppProperties properties;

    public <T> T execute(Long productId, Supplier<T> action) {
        RLock lock = redissonClient.getLock(LockKeys.product(productId));
        boolean acquired = false;
        try {
            acquired = lock.tryLock(
                    properties.lock().waitTime().toMillis(),
                    properties.lock().leaseTime().toMillis(),
                    TimeUnit.MILLISECONDS);
            if (!acquired) {
                throw new LockNotAcquiredException(productId);
            }
            return transactionTemplate.execute(status -> action.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockNotAcquiredException(productId);
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
