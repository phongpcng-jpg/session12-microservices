package com.rikkei.order.client;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/products/{id}")
    ProductView getById(@PathVariable("id") Long id);

    record ProductView(Long id, String name, BigDecimal effectivePrice) {
    }
}
