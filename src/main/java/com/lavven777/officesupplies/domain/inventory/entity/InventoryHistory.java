package com.lavven777.officesupplies.domain.inventory.entity;

import com.lavven777.officesupplies.domain.item.entity.Item;
import com.lavven777.officesupplies.domain.user.entity.User;
import com.lavven777.officesupplies.global.common.BaseCreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 재고 변경 이력 엔티티.
 *
 * 재고가 변경될 때마다 한 행이 INSERT된다.
 * 이 엔티티는 기록 전용이며, 생성 이후 절대 수정되지 않는다.
 *
 * BaseCreatedEntity를 상속해 createdAt만 관리한다.
 * updated_at은 의도적으로 제외 — 불변 이력 데이터에 수정 시각은 의미 없다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "inventory_histories")
public class InventoryHistory extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 재고가 변경된 비품.
     *
     * fetch = LAZY: 이력 목록 조회 시 Item을 즉시 로딩하지 않는다.
     * 필요한 경우 JPQL fetch join으로 함께 조회한다.
     * nullable = false: 어느 비품의 이력인지 반드시 기록해야 한다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * 이력 유형.
     *
     * INBOUND    — 입고. 관리자가 재고를 직접 추가한 경우.
     * OUTBOUND   — 출고. 요청 승인으로 재고가 차감된 경우.
     * ADJUSTMENT — 재고 조정. 실사 후 수량을 직접 보정한 경우.
     *
     * EnumType.STRING: DB에 "INBOUND" 문자열로 저장.
     * EnumType.ORDINAL은 Enum 순서 변경 시 데이터가 깨지므로 사용하지 않는다.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HistoryType historyType;

    /**
     * 변경 수량.
     *
     * 항상 양수로 저장한다.
     * 입고/출고 구분은 historyType으로 표현하므로
     * quantity에 음수를 저장하지 않는다.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * 변경 전 재고.
     *
     * 이 이력이 기록될 당시 Item.currentStock의 값.
     * afterStock과 함께 저장해 이력 하나만으로 전후 상태를 파악할 수 있다.
     */
    @Column(nullable = false)
    private Integer beforeStock;

    /**
     * 변경 후 재고.
     *
     * 재고 변경 직후 Item.currentStock의 값.
     * beforeStock - quantity = afterStock (OUTBOUND 기준) 검증에 활용 가능.
     */
    @Column(nullable = false)
    private Integer afterStock;

    /**
     * 재고를 변경한 처리자 (관리자).
     *
     * nullable = false: "누가 처리했는지"는 이력에서 반드시 기록해야 한다.
     * fetch = LAZY: 이력 조회 시 User를 즉시 로딩하지 않는다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /*
     * createdAt — BaseCreatedEntity의 @CreatedDate가 INSERT 시점에 자동 주입.
     * 직접 선언하거나 생성자에서 설정하지 않아도 된다.
     */

    /**
     * @Builder를 클래스가 아닌 생성자에 직접 적용.
     *
     * 이유: 클래스 레벨에 붙이면 id, createdAt 같은 자동 관리 필드까지
     * 빌더 파라미터로 노출된다. 생성자에 붙이면 외부에서 세팅 가능한
     * 필드를 명시적으로 제한할 수 있다.
     */
    @Builder
    public InventoryHistory(Item item, HistoryType historyType,
                            Integer quantity, Integer beforeStock,
                            Integer afterStock, User createdBy) {
        this.item = item;
        this.historyType = historyType;
        this.quantity = quantity;
        this.beforeStock = beforeStock;
        this.afterStock = afterStock;
        this.createdBy = createdBy;
        // createdAt은 @CreatedDate가 자동 처리 — 생성자에서 설정하지 않는다.
    }
}
