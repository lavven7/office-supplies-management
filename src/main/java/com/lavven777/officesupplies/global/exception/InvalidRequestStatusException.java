package com.lavven777.officesupplies.global.exception;

/**
 * 허용되지 않는 상태 전이 시 발생하는 예외.
 *
 * 발생 상황 예시:
 * - 이미 승인된 요청을 다시 승인하려는 경우
 * - 이미 반려된 요청을 취소하려는 경우
 * - REQUESTED 상태가 아닌 요청에 approve/reject/cancel 호출 시
 *
 * ItemRequest.validateStatus()에서 throw.
 */
public class InvalidRequestStatusException extends BusinessException {

    public InvalidRequestStatusException() {
        super(ErrorCode.INVALID_REQUEST_STATUS);
    }
}
