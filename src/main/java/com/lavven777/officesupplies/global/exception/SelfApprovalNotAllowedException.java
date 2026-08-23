package com.lavven777.officesupplies.global.exception;

public class SelfApprovalNotAllowedException extends BusinessException {

    public SelfApprovalNotAllowedException() {
        super(ErrorCode.SELF_APPROVAL_NOT_ALLOWED);
    }
}
