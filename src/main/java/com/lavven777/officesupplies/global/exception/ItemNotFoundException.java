package com.lavven777.officesupplies.global.exception;

public class ItemNotFoundException extends BusinessException {

    public ItemNotFoundException() {
        super(ErrorCode.ITEM_NOT_FOUND);
    }
}