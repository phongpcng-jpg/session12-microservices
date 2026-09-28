package com.rikkei.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductUpdateRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 5000) String description,
        @Size(max = 500) String imageUrl,
        @NotNull @DecimalMin("0.0") BigDecimal price) {
}
