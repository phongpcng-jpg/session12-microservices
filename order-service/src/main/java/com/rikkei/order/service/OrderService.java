package com.rikkei.order.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.rikkei.order.client.InventoryClient;
import com.rikkei.order.client.ProductClient;
import com.rikkei.order.domain.Order;
import com.rikkei.order.domain.OrderStatus;
import com.rikkei.order.dto.CreateOrderRequest;
import com.rikkei.order.dto.OrderResponse;
import com.rikkei.order.exception.DependencyUnavailableException;
import com.rikkei.order.exception.OrderNotFoundException;
import com.rikkei.order.exception.OutOfStockException;
import com.rikkei.order.exception.ProductNotFoundException;
import com.rikkei.order.mapper.OrderMapper;
import com.rikkei.order.repository.OrderRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final OrderEventPublisher eventPublisher;
    private final OrderMapper orderMapper;

    public OrderResponse place(CreateOrderRequest request) {
        ProductClient.ProductView product = fetchProduct(request.productId());
        UUID reservationId = UUID.randomUUID();
        reserveStock(reservationId, request);

        Order order = persist(reservationId, request, product.effectivePrice());

        try {
            eventPublisher.publish(orderMapper.toEvent(order));
        } catch (RuntimeException e) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            releaseQuietly(reservationId);
            throw e;
        }
        return orderMapper.toResponse(order);
    }

    public OrderResponse get(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private ProductClient.ProductView fetchProduct(Long productId) {
        try {
            return productClient.getById(productId);
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException(productId);
        } catch (FeignException e) {
            throw new DependencyUnavailableException("product-service không khả dụng", e);
        }
    }

    private void reserveStock(UUID reservationId, CreateOrderRequest request) {
        try {
            inventoryClient.reserve(new InventoryClient.ReserveCommand(
                    reservationId, request.productId(), request.quantity()));
        } catch (FeignException.Conflict e) {
            throw new OutOfStockException();
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException(request.productId());
        } catch (FeignException e) {
            throw new DependencyUnavailableException("inventory-service không khả dụng", e);
        }
    }

    private Order persist(UUID reservationId, CreateOrderRequest request, BigDecimal unitPrice) {
        Order order = new Order();
        order.setReservationId(reservationId);
        order.setProductId(request.productId());
        order.setQuantity(request.quantity());
        order.setCustomerEmail(request.customerEmail());
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(unitPrice.multiply(BigDecimal.valueOf(request.quantity())));
        order.setStatus(OrderStatus.CREATED);
        try {
            return orderRepository.saveAndFlush(order);
        } catch (RuntimeException e) {
            releaseQuietly(reservationId);
            throw e;
        }
    }

    private void releaseQuietly(UUID reservationId) {
        try {
            inventoryClient.release(reservationId);
        } catch (RuntimeException e) {
            log.error("Failed to release reservation {}", reservationId, e);
        }
    }
}
