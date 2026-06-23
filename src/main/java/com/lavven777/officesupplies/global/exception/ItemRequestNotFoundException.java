package com.lavven777.officesupplies.global.exception;

public class ItemRequestNotFoundException extends BusinessException {

    public ItemRequestNotFoundException() {
        super(ErrorCode.ITEM_REQUEST_NOT_FOUND);
    }
}