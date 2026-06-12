package com.lavven777.officesupplies.global.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 예외의 최상위 클래스.
 *
 * 모든 도메인 예외는 이 클래스를 상속한다.
 * ErrorCode를 반드시 포함해 HTTP 상태 코드와 메시지를 일관되게 관리한다.
 *
 * 상속 구조:
 * BusinessException
 * ├── InsufficientStockException       (INSUFFICIENT_STOCK)
 * ├── InvalidRequestStatusException    (INVALID_REQUEST_STATUS)
 * ├── InvalidQuantityException         (INVALID_QUANTITY)
 * ├── UserNotFoundException            (USER_NOT_FOUND)
 * ├── ItemNotFoundException            (ITEM_NOT_FOUND)
 * └── ...
 *
 * GlobalExceptionHandler에서 BusinessException 하나만 잡아도
 * 모든 하위 예외를 일관된 방식으로 처리할 수 있다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
