package com.lavven777.officesupplies.domain.itemrequest.entity;

import com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequestDetail;
import com.lavven777.officesupplies.domain.user.entity.User;
import com.lavven777.officesupplies.global.common.BaseTimeEntity;
import com.lavven777.officesupplies.global.exception.InvalidRequestStatusException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequestStatus.*;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "item_requests")
public class ItemRequest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /**
     * 요청자. 비품을 요청한 직원.
     * nullable = false — 요청자 없는 요청은 존재할 수 없음.
     * fetch = LAZY — 요청 목록 조회 시 불필요한 User 쿼리 방지.
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    /**
     * 요청 상태. 기본값 REQUESTED.
     * EnumType.STRING — DB에 "REQUESTED" 문자열로 저장.
     * EnumType.ORDINAL 사용 시 Enum 순서 변경에 취약하므로 STRING 권장.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ItemRequestStatus status;

    /**
     * 승인 또는 반려 처리 시각.
     * 요청 생성 시점은 BaseTimeEntity.createdAt으로 표현.
     * nullable — 아직 처리되지 않은 요청은 null.
     */
    private LocalDateTime approvalDate;

    /**
     * 승인자 또는 반려자. 처리한 관리자.
     * nullable = true — 아직 처리되지 않은 요청은 null.
     * fetch = LAZY — 필요한 경우에만 조회.
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "approver_id")
    private User approver;

    /**
     * 반려 사유. 반려 시에만 값이 존재.
     * nullable — 승인/대기/취소 상태에서는 null.
     */
    @Column(length = 500)
    private String rejectReason;

    /**
     * 요청에 포함된 비품 목록 (명세 라인).
     * cascade = ALL — ItemRequest 저장/삭제 시 ItemRequestDetail도 함께 처리.
     * orphanRemoval = true — 리스트에서 제거된 Detail은 DB에서도 삭제.
     */
    @OneToMany(mappedBy = "itemRequest", cascade = ALL, orphanRemoval = true)
    private List<ItemRequestDetail> details = new ArrayList<>();

    @Builder
    public ItemRequest(User requester) {
        this.requester = requester;
        this.status = REQUESTED;
    }

    // -----------------------------------------------------------------------
    // 연관관계 편의 메서드
    // -----------------------------------------------------------------------

    /**
     * ItemRequestDetail 추가.
     * 양방향 관계를 한 번에 설정 — detail.setItemRequest()를 직접 호출하지 않아도 됨.
     */
    public void addDetail(ItemRequestDetail detail) {
        this.details.add(detail);
        detail.assignItemRequest(this);
    }

    // -----------------------------------------------------------------------
    // 비즈니스 메서드 — 상태 전이
    // -----------------------------------------------------------------------

    /**
     * 요청 승인.
     * REQUESTED 상태에서만 호출 가능.
     * 승인자와 승인 시각을 기록하고 상태를 APPROVED로 변경한다.
     */
    public void approve(User approver) {
        validateStatus(REQUESTED);
        this.status = APPROVED;
        this.approver = approver;
        this.approvalDate = LocalDateTime.now();
    }

    /**
     * 요청 반려.
     * REQUESTED 상태에서만 호출 가능.
     * rejectReason은 필수 — 직원에게 반려 이유를 알려야 함.
     */
    public void reject(User approver, String rejectReason) {
        validateStatus(REQUESTED);
        this.status = REJECTED;
        this.approver = approver;
        this.rejectReason = rejectReason;
        this.approvalDate = LocalDateTime.now();
    }

    /**
     * 요청 취소.
     * REQUESTED 상태에서만 호출 가능 — 이미 승인된 요청은 취소 불가.
     * 요청자 본인만 취소할 수 있는지는 Service 레이어에서 검증.
     */
    public void cancel() {
        validateStatus(REQUESTED);
        this.status = CANCELED;
    }

    // -----------------------------------------------------------------------
    // private 헬퍼
    // -----------------------------------------------------------------------

    /**
     * 상태 전이 가능 여부 검증.
     * 기대 상태가 아니면 InvalidRequestStatusException 발생.
     */
    private void validateStatus(ItemRequestStatus expected) {
        if (this.status != expected) {
            throw new InvalidRequestStatusException();
        }
    }
}
