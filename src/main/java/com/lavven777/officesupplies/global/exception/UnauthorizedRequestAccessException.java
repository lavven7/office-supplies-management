package com.lavven777.officesupplies.global.exception;

public class UnauthorizedRequestAccessException extends BusinessException {

    public UnauthorizedRequestAccessException() {
        super(ErrorCode.UNAUTHORIZED_REQUEST_ACCESS);
    }
}
