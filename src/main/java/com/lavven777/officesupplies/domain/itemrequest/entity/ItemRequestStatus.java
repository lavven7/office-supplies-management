package com.lavven777.officesupplies.domain.itemrequest.entity;

public enum ItemRequestStatus {

    REQUESTED("요청 대기"),
    APPROVED("승인"),
    REJECTED("반려"),
    CANCELED("취소");

    private final String displayName;

    ItemRequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}