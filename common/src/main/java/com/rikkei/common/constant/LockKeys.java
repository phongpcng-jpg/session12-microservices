package com.rikkei.common.constant;

public final class LockKeys {

    private static final String PRODUCT_PREFIX = "lock:product:";

    private LockKeys() {
    }

    public static String product(Long productId) {
        return PRODUCT_PREFIX + productId;
    }
}
