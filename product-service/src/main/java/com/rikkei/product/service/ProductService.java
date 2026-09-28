package com.rikkei.product.service;

import java.math.BigDecimal;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rikkei.common.constant.CacheNames;
import com.rikkei.product.client.PromotionGateway;
import com.rikkei.product.config.AppProperties;
import com.rikkei.product.domain.Product;
import com.rikkei.product.dto.ProductResponse;
import com.rikkei.product.dto.ProductUpdateRequest;
import com.rikkei.product.mapper.ProductMapper;
import com.rikkei.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final PromotionGateway promotionGateway;
    private final ProductMapper productMapper;
    private final AppProperties properties;

    @Cacheable(cacheNames = CacheNames.PRODUCT, key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        simulateDatabaseLatency();
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        BigDecimal promotionPrice = promotionGateway.activePrice(id).orElse(null);
        return productMapper.toResponse(product, promotionPrice);
    }

    @CacheEvict(cacheNames = CacheNames.PRODUCT, key = "#id")
    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        productMapper.apply(request, product);
        Product saved = productRepository.saveAndFlush(product);
        BigDecimal promotionPrice = promotionGateway.activePrice(id).orElse(null);
        return productMapper.toResponse(saved, promotionPrice);
    }

    private void simulateDatabaseLatency() {
        long millis = properties.simulatedDbLatency().toMillis();
        if (millis <= 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
