package com.lavven777.officesupplies.global.exception;

public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String itemName) {
        super(ErrorCode.INSUFFICIENT_STOCK);
    }
}