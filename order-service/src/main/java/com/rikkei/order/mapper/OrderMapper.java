package com.rikkei.order.mapper;

import org.mapstruct.Mapper;

import com.rikkei.common.event.OrderCreatedEvent;
import com.rikkei.order.domain.Order;
import com.rikkei.order.dto.OrderResponse;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order order);

    default OrderCreatedEvent toEvent(Order order) {
        return new OrderCreatedEvent(
                order.getId(),
                order.getReservationId(),
                order.getProductId(),
                order.getQuantity(),
                order.getCustomerEmail(),
                order.getTotalAmount(),
                order.getCreatedAt());
    }
}
