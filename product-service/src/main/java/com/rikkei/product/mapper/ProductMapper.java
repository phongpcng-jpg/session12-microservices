package com.rikkei.product.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.rikkei.product.domain.Product;
import com.rikkei.product.dto.ProductResponse;
import com.rikkei.product.dto.ProductUpdateRequest;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "promotionPrice", source = "promotionPrice")
    @Mapping(target = "effectivePrice", expression = "java(promotionPrice != null ? promotionPrice : product.getPrice())")
    ProductResponse toResponse(Product product, BigDecimal promotionPrice);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void apply(ProductUpdateRequest request, @MappingTarget Product product);
}
