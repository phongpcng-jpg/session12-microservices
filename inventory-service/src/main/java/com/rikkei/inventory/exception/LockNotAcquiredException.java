package com.rikkei.inventory.exception;

public class LockNotAcquiredException extends RuntimeException {

    public LockNotAcquiredException(Long productId) {
        super("Hệ thống đang bận, vui lòng thử lại: " + productId);
    }
}
