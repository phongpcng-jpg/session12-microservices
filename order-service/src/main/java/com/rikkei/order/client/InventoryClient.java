package com.rikkei.order.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PostMapping("/inventory/reserve")
    ReservationView reserve(@RequestBody ReserveCommand command);

    @PostMapping("/inventory/reservations/{reservationId}/release")
    ReservationView release(@PathVariable("reservationId") UUID reservationId);

    record ReserveCommand(UUID reservationId, Long productId, int quantity) {
    }

    record ReservationView(UUID id, Long productId, int quantity, String status) {
    }
}
