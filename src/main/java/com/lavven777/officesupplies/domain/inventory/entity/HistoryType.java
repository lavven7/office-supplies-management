package com.lavven777.officesupplies.domain.inventory.entity;

public enum HistoryType {

    INBOUND("입고"),
    OUTBOUND("출고"),
    ADJUSTMENT("재고 조정");

    private final String displayName;

    HistoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}