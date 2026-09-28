package com.rikkei.inventory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.rikkei.inventory.dto.ReserveRequest;
import com.rikkei.inventory.exception.OutOfStockException;
import com.rikkei.inventory.service.InventoryService;

@Testcontainers
@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.kafka.listener.auto-startup=false"
})
class InventoryConcurrencyTest {

    private static final long PRODUCT_ID = 2L;
    private static final int BUYERS = 100;

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    InventoryService inventoryService;

    @Test
    void onlyAvailableStockIsSold() throws Exception {
        inventoryService.updateQuantity(PRODUCT_ID, 1);

        AtomicInteger succeeded = new AtomicInteger();
        AtomicInteger outOfStock = new AtomicInteger();
        CountDownLatch ready = new CountDownLatch(BUYERS);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(BUYERS);

        for (int i = 0; i < BUYERS; i++) {
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    inventoryService.reserve(new ReserveRequest(UUID.randomUUID(), PRODUCT_ID, 1));
                    succeeded.incrementAndGet();
                } catch (OutOfStockException e) {
                    outOfStock.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        ready.await();
        start.countDown();
        executor.shutdown();
        assertThat(executor.awaitTermination(60, TimeUnit.SECONDS)).isTrue();

        assertThat(succeeded.get()).isEqualTo(1);
        assertThat(outOfStock.get()).isEqualTo(BUYERS - 1);
        assertThat(inventoryService.getStock(PRODUCT_ID).quantity()).isZero();
    }
}
