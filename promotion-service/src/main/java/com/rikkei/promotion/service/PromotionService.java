package com.rikkei.promotion.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rikkei.promotion.domain.Promotion;
import com.rikkei.promotion.dto.PromotionRequest;
import com.rikkei.promotion.dto.PromotionResponse;
import com.rikkei.promotion.mapper.PromotionMapper;
import com.rikkei.promotion.repository.PromotionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public PromotionResponse getByProductId(Long productId) {
        return promotionRepository.findByProductId(productId)
                .map(promotionMapper::toResponse)
                .orElseThrow(() -> new PromotionNotFoundException(productId));
    }

    @Transactional
    public PromotionResponse upsert(Long productId, PromotionRequest request) {
        Promotion promotion = promotionRepository.findByProductId(productId).orElseGet(() -> {
            Promotion created = new Promotion();
            created.setProductId(productId);
            return created;
        });
        promotionMapper.apply(request, promotion);
        Promotion saved = promotionRepository.saveAndFlush(promotion);
        eventPublisher.publishEvent(new PromotionChangedEvent(productId));
        return promotionMapper.toResponse(saved);
    }
}
