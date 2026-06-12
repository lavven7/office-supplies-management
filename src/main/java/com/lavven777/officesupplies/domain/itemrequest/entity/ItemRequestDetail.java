package com.lavven777.officesupplies.domain.itemrequest.entity;

import com.lavven777.officesupplies.domain.item.entity.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "item_request_details")
public class ItemRequestDetail {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /**
     * 소속 요청서.
     * mappedBy = "itemRequest" 의 연관관계 주인 — FK(item_request_id)를 이 쪽에서 관리.
     * nullable = false — Detail은 반드시 요청서에 속해야 함.
     * fetch = LAZY — 명세 라인 단독 조회 시 불필요한 ItemRequest 로딩 방지.
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_request_id", nullable = false)
    private ItemRequest itemRequest;

    /**
     * 요청 대상 비품.
     * nullable = false — 비품 없는 명세 라인은 의미 없음.
     * fetch = LAZY — 필요한 경우에만 Item 조회.
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * 요청 수량. 0보다 커야 함.
     * nullable = false — 수량 없는 요청 라인은 의미 없음.
     */
    @Column(nullable = false)
    private Integer quantity;

    @Builder
    public ItemRequestDetail(Item item, Integer quantity) {
        this.item = item;
        this.quantity = quantity;
        // itemRequest는 ItemRequest.addDetail()을 통해 설정 — 직접 세팅 금지
    }

    /**
     * 연관관계 편의 메서드에서만 호출.
     * package-private — 외부에서 직접 호출하지 못하도록 접근 제어.
     * ItemRequest.addDetail() 내부에서만 사용.
     */
    void assignItemRequest(ItemRequest itemRequest) {
        this.itemRequest = itemRequest;
    }
}
