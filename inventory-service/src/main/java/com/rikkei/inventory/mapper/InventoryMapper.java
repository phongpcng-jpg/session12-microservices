package com.rikkei.inventory.mapper;

import org.mapstruct.Mapper;

import com.rikkei.inventory.domain.Reservation;
import com.rikkei.inventory.domain.Stock;
import com.rikkei.inventory.dto.ReservationResponse;
import com.rikkei.inventory.dto.StockResponse;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    ReservationResponse toResponse(Reservation reservation);

    StockResponse toResponse(Stock stock);
}
