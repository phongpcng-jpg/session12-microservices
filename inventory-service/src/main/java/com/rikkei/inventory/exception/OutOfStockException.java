package com.rikkei.inventory.exception;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException() {
        super("Sản phẩm đã hết hàng");
    }
}
