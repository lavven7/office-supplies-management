package com.lavven777.officesupplies.global.exception;


import lombok.Getter;

/**
 * 시스템 전체 에러 코드 정의.
 *
 * HTTP 상태 코드와 사용자에게 보여줄 메시지를 함께 관리.
 * 새 예외 추가 시 여기에 항목을 먼저 등록하고 예외 클래스를 만드는 순서로 진행.
 */
@Getter
public enum ErrorCode {

    // -----------------------------------------------------------------------
    // User (1xx)
    // -----------------------------------------------------------------------
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(409, "이미 사용 중인 이메일입니다."),
    DUPLICATE_EMPLOYEE_NUMBER(409, "이미 사용 중인 사번입니다."),

    // -----------------------------------------------------------------------
    // Item (2xx)
    // -----------------------------------------------------------------------
    ITEM_NOT_FOUND(404, "비품을 찾을 수 없습니다."),
    ITEM_NOT_ACTIVE(400, "비활성화된 비품입니다."),
    INSUFFICIENT_STOCK(400, "재고가 부족합니다."),

    // -----------------------------------------------------------------------
    // ItemRequest (3xx)
    // -----------------------------------------------------------------------
    ITEM_REQUEST_NOT_FOUND(404, "요청을 찾을 수 없습니다."),
    INVALID_REQUEST_STATUS(400, "현재 상태에서는 처리할 수 없는 요청입니다."),
    INVALID_QUANTITY(400, "수량은 1 이상이어야 합니다."),
    UNAUTHORIZED_REQUEST_ACCESS(403, "해당 요청에 대한 권한이 없습니다."),

    // -----------------------------------------------------------------------
    // InventoryHistory (4xx)
    // -----------------------------------------------------------------------
    INVENTORY_HISTORY_NOT_FOUND(404, "재고 이력을 찾을 수 없습니다.");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
