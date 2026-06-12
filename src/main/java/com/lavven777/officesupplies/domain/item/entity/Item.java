package com.lavven777.officesupplies.domain.item.entity;

import com.lavven777.officesupplies.global.common.BaseTimeEntity;
import com.lavven777.officesupplies.global.exception.InsufficientStockException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "items")
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 비품명. null 불가, 최대 100자.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 비품 설명. 긴 텍스트 허용 (@Lob → TEXT 타입 매핑).
     * 선택 입력이므로 nullable.
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 카테고리 (예: 사무용품, 전자기기).
     * Enum으로 관리할 수도 있으나 MVP에서는 단순 문자열로 처리.
     */
    @Column(length = 50)
    private String category;

    /**
     * 단가. DECIMAL(10,2) 매핑.
     * BigDecimal 사용 이유: 금액 계산 시 부동소수점 오차 방지.
     * 선택 입력이므로 nullable.
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /**
     * 현재 재고 수량. 0 이상이어야 하며 null 불가.
     */
    @Column(nullable = false)
    private Integer currentStock;

    /**
     * 최소 재고 기준. 현재 재고가 이 값 이하이면 재고 부족 알림 대상.
     */
    @Column(nullable = false)
    private Integer minimumStock;

    /**
     * 활성화 여부. 삭제 대신 비활성화(Soft Delete)로 처리.
     * false이면 직원 목록에서 노출되지 않음.
     */
    @Column(nullable = false)
    private boolean active;

    @Builder
    public Item(String name, String description, String category,
                BigDecimal unitPrice, Integer currentStock, Integer minimumStock) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.unitPrice = unitPrice;
        this.currentStock = (currentStock != null) ? currentStock : 0;
        this.minimumStock = (minimumStock != null) ? minimumStock : 0;
        this.active = true; // 등록 시 항상 활성 상태
    }

    // -----------------------------------------------------------------------
    // 비즈니스 메서드
    // -----------------------------------------------------------------------

    /**
     * 재고 차감.
     * 승인 처리 시 ItemRequestService에서 호출.
     * 재고 부족 시 InsufficientStockException 발생 → 트랜잭션 롤백.
     */
    public void decreaseStock(int quantity) {
        if (this.currentStock < quantity) {
            throw new InsufficientStockException(this.name);
        }
        this.currentStock -= quantity;
    }

    /**
     * 재고 증가.
     * 입고(INBOUND) 또는 재고 조정(ADJUSTMENT) 처리 시 호출.
     */
    public void increaseStock(int quantity) {
        this.currentStock += quantity;
    }

    /**
     * 재고 부족 여부 확인.
     * currentStock <= minimumStock 이면 true.
     * 대시보드 재고 부족 알림에서 사용.
     */
    public boolean isLowStock() {
        return this.currentStock <= this.minimumStock;
    }

    /**
     * 비품 비활성화 (Soft Delete).
     * 실제 데이터는 유지하고 active = false로 변경.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * 비품 정보 수정.
     * 수정 가능한 필드만 허용 — id, active는 이 메서드로 변경 불가.
     */
    public void update(String name, String description, String category,
                       BigDecimal unitPrice, Integer minimumStock) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.unitPrice = unitPrice;
        this.minimumStock = minimumStock;
    }
}