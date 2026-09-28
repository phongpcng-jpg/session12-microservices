package com.rikkei.inventory.dto;

import java.util.UUID;

import com.rikkei.inventory.domain.ReservationStatus;

public record ReservationResponse(UUID id, Long productId, int quantity, ReservationStatus status) {
}
