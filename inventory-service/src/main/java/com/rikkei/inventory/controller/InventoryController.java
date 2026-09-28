package com.rikkei.inventory.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rikkei.inventory.dto.ReservationResponse;
import com.rikkei.inventory.dto.ReserveRequest;
import com.rikkei.inventory.dto.StockRequest;
import com.rikkei.inventory.dto.StockResponse;
import com.rikkei.inventory.service.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public StockResponse getStock(@PathVariable Long productId) {
        return inventoryService.getStock(productId);
    }

    @PutMapping("/{productId}")
    public StockResponse updateQuantity(@PathVariable Long productId, @Valid @RequestBody StockRequest request) {
        return inventoryService.updateQuantity(productId, request.quantity());
    }

    @PostMapping("/reserve")
    public ReservationResponse reserve(@Valid @RequestBody ReserveRequest request) {
        return inventoryService.reserve(request);
    }

    @PostMapping("/reservations/{reservationId}/release")
    public ReservationResponse release(@PathVariable UUID reservationId) {
        return inventoryService.release(reservationId);
    }
}
