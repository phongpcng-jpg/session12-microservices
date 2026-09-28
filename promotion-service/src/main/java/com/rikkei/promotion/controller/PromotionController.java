package com.rikkei.promotion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rikkei.promotion.dto.PromotionRequest;
import com.rikkei.promotion.dto.PromotionResponse;
import com.rikkei.promotion.service.PromotionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping("/{productId}")
    public PromotionResponse get(@PathVariable Long productId) {
        return promotionService.getByProductId(productId);
    }

    @PutMapping("/{productId}")
    public PromotionResponse upsert(@PathVariable Long productId, @Valid @RequestBody PromotionRequest request) {
        return promotionService.upsert(productId, request);
    }
}
