package com.rikkei.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rikkei.order.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
