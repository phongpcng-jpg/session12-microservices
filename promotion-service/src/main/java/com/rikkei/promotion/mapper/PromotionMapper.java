package com.rikkei.promotion.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

import com.rikkei.promotion.domain.Promotion;
import com.rikkei.promotion.dto.PromotionRequest;
import com.rikkei.promotion.dto.PromotionResponse;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    PromotionResponse toResponse(Promotion promotion);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void apply(PromotionRequest request, @MappingTarget Promotion promotion);
}
