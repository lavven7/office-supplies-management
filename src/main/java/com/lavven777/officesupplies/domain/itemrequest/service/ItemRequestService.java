package com.lavven777.officesupplies.domain.itemrequest.service;

import com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequest;
import com.lavven777.officesupplies.domain.itemrequest.repository.ItemRequestRepository;
import com.lavven777.officesupplies.domain.user.entity.User;
import com.lavven777.officesupplies.domain.user.repository.UserRepository;
import com.lavven777.officesupplies.global.exception.ItemRequestNotFoundException;
import com.lavven777.officesupplies.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    // 2단계에서 추가 예정
    // private final InventoryHistoryRepository inventoryHistoryRepository;

    /**
     * [1단계] 승인자 조회 → 요청 조회 → 상태 변경까지만 구현.
     * 2단계에서 재고 차감 + 이력 저장을 추가할 예정.
     */
    @Transactional
    public void approveRequest(Long requestId, Long approverId) {

        // ① 승인자 조회
        // approverId로 DB에서 User를 가져온다.
        // 없으면 UserNotFoundException 발생.
        //
        // 왜 Security Context에서 꺼내지 않는가:
        // Security Context의 인증 객체는 로그인 시점의 스냅샷이다.
        // DB에서 직접 조회해야 최신 상태(권한 변경 등)가 반영된다.
        User approver = userRepository.findById(approverId)
                .orElseThrow(UserNotFoundException::new);

        // ② ItemRequest 조회 — details + item을 fetch join으로 함께 로딩
        // 없으면 ItemRequestNotFoundException 발생.
        //
        // 왜 findById() 대신 findByIdWithDetails()를 쓰는가:
        // 2단계에서 detail.getItem()에 접근할 때 N+1 문제가 발생한다.
        // JOIN FETCH로 미리 로딩해두면 반복문 안에서 추가 쿼리가 발생하지 않는다.
        // 1단계에서는 details를 쓰지 않지만, 메서드는 그대로 유지한다.
        ItemRequest itemRequest = itemRequestRepository.findByIdWithDetails(requestId)
                .orElseThrow(ItemRequestNotFoundException::new);

        // ③ 상태 변경
        // ItemRequest.approve() 내부에서 두 가지를 처리한다.
        //   - validateStatus(REQUESTED): REQUESTED가 아니면 InvalidRequestStatusException 발생
        //   - status = APPROVED, approver 세팅, approvalDate = now()
        //
        // 왜 Service에서 직접 status를 바꾸지 않는가:
        // 상태 전이 규칙은 도메인 지식이므로 Entity가 직접 책임진다.
        // Service에서 if 문으로 체크하면 같은 규칙이 여러 곳에 흩어진다.
        itemRequest.approve(approver);

        // itemRequest의 변경사항(status, approver, approvalDate)은
        // 별도 save() 없이 자동 반영된다.
        //
        // 이유: @Transactional 범위 안에서 조회한 엔티티는 영속 상태이고,
        // 트랜잭션 커밋 시점에 JPA 변경 감지(dirty checking)가
        // 변경된 필드를 감지해 UPDATE 쿼리를 자동 실행한다.

        // 2단계에서 추가 예정:
        // for (ItemRequestDetail detail : itemRequest.getDetails()) { ... }
    }
}
