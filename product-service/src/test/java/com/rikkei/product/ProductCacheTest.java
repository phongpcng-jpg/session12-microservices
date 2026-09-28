package com.rikkei.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.rikkei.common.constant.CacheNames;
import com.rikkei.common.constant.Channels;
import com.rikkei.product.client.PromotionGateway;
import com.rikkei.product.dto.ProductUpdateRequest;
import com.rikkei.product.service.ProductService;

@Testcontainers
@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "app.simulated-db-latency=200ms"
})
class ProductCacheTest {

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

    @MockitoBean
    PromotionGateway promotionGateway;

    @Autowired
    ProductService productService;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    StringRedisTemplate redisTemplate;

    private long timed(Runnable action) {
        long start = System.nanoTime();
        action.run();
        return Duration.ofNanos(System.nanoTime() - start).toMillis();
    }

    @Test
    void secondReadIsServedFromRedis() {
        stubNoPromotion();
        cacheManager.getCache(CacheNames.PRODUCT).clear();

        long first = timed(() -> productService.getById(1L));
        long second = timed(() -> productService.getById(1L));

        assertThat(first).isGreaterThanOrEqualTo(200);
        assertThat(second).isLessThan(100);
    }

    @Test
    void updateEvictsCache() {
        stubNoPromotion();
        cacheManager.getCache(CacheNames.PRODUCT).clear();
        productService.getById(3L);
        assertThat(cacheManager.getCache(CacheNames.PRODUCT).get(3L)).isNotNull();

        productService.update(3L, new ProductUpdateRequest("Omega 3 Fish Oil", "Updated", null, new BigDecimal("500000")));

        assertThat(cacheManager.getCache(CacheNames.PRODUCT).get(3L)).isNull();
    }

    @Test
    void pubSubMessageEvictsCache() {
        stubNoPromotion();
        Cache cache = cacheManager.getCache(CacheNames.PRODUCT);
        cache.clear();
        productService.getById(2L);
        assertThat(cache.get(2L)).isNotNull();

        redisTemplate.convertAndSend(Channels.PROMOTION_UPDATES, "2");

        Awaitility.await().atMost(Duration.ofSeconds(1)).untilAsserted(() -> assertThat(cache.get(2L)).isNull());
    }

    private void stubNoPromotion() {
        org.mockito.Mockito.when(promotionGateway.activePrice(org.mockito.ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
    }
}
