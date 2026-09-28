package com.rikkei.promotion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rikkei.promotion.domain.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByProductId(Long productId);
}
