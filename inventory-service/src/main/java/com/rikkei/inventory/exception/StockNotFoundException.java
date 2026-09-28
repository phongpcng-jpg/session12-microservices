package com.rikkei.inventory.exception;

public class StockNotFoundException extends RuntimeException {

    public StockNotFoundException(Long productId) {
        super("Stock not found for product: " + productId);
    }
}
