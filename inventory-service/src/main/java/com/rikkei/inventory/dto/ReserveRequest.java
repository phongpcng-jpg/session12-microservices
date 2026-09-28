package com.rikkei.inventory.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReserveRequest(
        @NotNull UUID reservationId,
        @NotNull Long productId,
        @Min(1) int quantity) {
}
