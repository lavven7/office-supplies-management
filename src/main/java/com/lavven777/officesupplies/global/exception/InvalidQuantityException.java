package com.lavven777.officesupplies.global.exception;

/**
 * 유효하지 않은 수량 값이 입력될 때 발생하는 예외.
 *
 * 발생 상황:
 * - ItemRequestDetail 생성 시 quantity가 null이거나 1 미만인 경우
 *
 * 검증 레이어 구분:
 * - DTO 레이어: @Min(1) 으로 사용자 입력 차단 (사용자 친화적 에러 메시지)
 * - Entity 레이어: 이 예외로 객체 불변식 보장 (어떤 경로로 생성해도 안전)
 */
public class InvalidQuantityException extends BusinessException {

    public InvalidQuantityException() {
        super(ErrorCode.INVALID_QUANTITY);
    }
}
