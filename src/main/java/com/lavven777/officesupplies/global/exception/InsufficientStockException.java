package com.lavven777.officesupplies.global.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String itemName) {
        super(itemName + "의 재고가 부족합니다.");
    }
}
