package com.rikkei.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rikkei.product.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
