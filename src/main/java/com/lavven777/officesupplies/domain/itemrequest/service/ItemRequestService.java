package com.lavven777.officesupplies.domain.itemrequest.service;

import com.lavven777.officesupplies.domain.inventory.entity.HistoryType;
import com.lavven777.officesupplies.domain.inventory.entity.InventoryHistory;
import com.lavven777.officesupplies.domain.inventory.repository.InventoryHistoryRepository;
import com.lavven777.officesupplies.domain.item.entity.Item;
import com.lavven777.officesupplies.domain.item.repository.ItemRepository;
import com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequest;
import com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequestDetail;
import com.lavven777.officesupplies.domain.itemrequest.repository.ItemRequestRepository;
import com.lavven777.officesupplies.domain.user.entity.Role;
import com.lavven777.officesupplies.domain.user.entity.User;
import com.lavven777.officesupplies.domain.user.repository.UserRepository;
import com.lavven777.officesupplies.global.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final InventoryHistoryRepository inventoryHistoryRepository;
    private final ItemRepository itemRepository;

    /**
     * 비품 요청을 승인한다.
     *
     * 요청 상태를 APPROVED로 변경하고,
     * 요청 상세 품목별로 재고를 차감한 뒤 재고 이력을 저장한다.
     *
     * 중간에 예외가 발생하면 @Transactional에 의해 전체 작업이 롤백된다.
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


        validateNotRequester(itemRequest, approver);

        itemRequest.approve(approver);

        // ③ 상태 변경
        // ItemRequest.approve() 내부에서 두 가지를 처리한다.
        //   - validateStatus(REQUESTED): REQUESTED가 아니면 InvalidRequestStatusException 발생
        //   - status = APPROVED, approver 세팅, approvalDate = now()
        //
        // 왜 Service에서 직접 status를 바꾸지 않는가:
        // 상태 전이 규칙은 도메인 지식이므로 Entity가 직접 책임진다.
        // Service에서 if 문으로 체크하면 같은 규칙이 여러 곳에 흩어진다.
        itemRequest.approve(approver);


        // ④ 품목별 재고 차감 + 이력 저장
        for (ItemRequestDetail detail : itemRequest.getDetails()) {

            Item item = detail.getItem();

            int beforeStock = item.getCurrentStock();

            item.decreaseStock(detail.getQuantity());

            int afterStock = item.getCurrentStock();

            InventoryHistory history = InventoryHistory.builder()
                    .item(item)
                    .historyType(HistoryType.OUTBOUND)
                    .quantity(detail.getQuantity())
                    .beforeStock(beforeStock)
                    .afterStock(afterStock)
                    .createdBy(approver)
                    .build();

            inventoryHistoryRepository.save(history);
        }


        // itemRequest와 item의 변경사항은 별도 save() 없이 자동 반영된다.
        // 이유: @Transactional 범위 안에서 조회한 엔티티는 영속 상태이고,
        // 트랜잭션 커밋 시점에 JPA 변경 감지(dirty checking)가
        // 변경된 필드를 감지해 UPDATE 쿼리를 자동 실행한다.


    } // approveRequest 끝

    private void validateNotRequester(ItemRequest itemRequest, User approver) {
        if (itemRequest.getRequester().getId().equals(approver.getId())) {
            throw new SelfApprovalNotAllowedException();
        }
    }

    /**
     * 비품 요청 반려 처리.
     *
     * approveRequest()와 구조가 동일하지만
     * 재고 차감과 이력 저장이 없다.
     * 반려는 재고에 영향을 주지 않는다.
     *
     * @param requestId    반려할 ItemRequest의 id
     * @param approverId   반려 처리하는 관리자 User의 id
     * @param rejectReason 반려 사유 — 필수값
     */

    @Transactional
    public void rejectRequest(Long requestId, Long approverId, String rejectReason) {

        // ① 반려자 조회
        User approver = userRepository.findById(approverId)
                .orElseThrow(UserNotFoundException::new);

        // ② 요청 조회
        // 반려는 details와 item에 접근하지 않으므로
        // findById()로 조회해도 N+1 문제가 발생하지 않는다.
        // 하지만 일관성을 위해 findByIdWithDetails()를 그대로 사용한다.
        ItemRequest itemRequest = itemRequestRepository.findByIdWithDetails(requestId)
                .orElseThrow(ItemRequestNotFoundException::new);

        validateNotRequester(itemRequest, approver);

        // ③ 상태 변경
        // ItemRequest.reject() 내부에서
        //   - validateStatus(REQUESTED) 검증
        //   - status = REJECTED
        //   - approver, rejectReason, approvalDate 세팅
        // 을 한 번에 처리한다.
        // save() 호출 없이 dirty checking으로 자동 UPDATE.
        itemRequest.reject(approver, rejectReason);
    }


    public List<ItemRequest> findAll() {
        return itemRequestRepository.findAll();
    }


    public ItemRequest findById(Long id) {
        return itemRequestRepository.findByIdWithDetails(id)
                .orElseThrow(ItemRequestNotFoundException::new);
    }

    /**
     * 비품 요청 생성.
     *
     * MVP 단계: 품목 하나만 받는 단순 구조.
     * 추후 여러 품목을 동시에 요청하는 구조로 확장 시
     * 파라미터를 List<> 또는 DTO로 교체.
     *
     * cascade = ALL 덕분에 itemRequestRepository.save() 하나로
     * ItemRequest + ItemRequestDetail 모두 INSERT된다.
     *
     * @param requesterId 요청자 User id (MVP: Controller에서 1L 하드코딩)
     * @param itemId      요청할 비품 id
     * @param quantity    요청 수량
     */
    @Transactional
    public void createRequest(Long requesterId, Long itemId, Integer quantity) {

        // ① 요청자 조회
        User requester = userRepository.findById(requesterId)
                .orElseThrow(UserNotFoundException::new);

        // ② 비품 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);

        // 비활성화된 비품으로 요청 방지
        // form에서 active 비품만 보여주지만 URL 직접 호출 등 우회 케이스 차단
        if (!item.isActive()) {
            throw new BusinessException(ErrorCode.ITEM_NOT_ACTIVE);
        }

        // ③ ItemRequestDetail 생성
        // 생성자 내부에서 quantity < 1 이면 InvalidQuantityException 발생
        ItemRequestDetail detail = ItemRequestDetail.builder()
                .item(item)
                .quantity(quantity)
                .build();

        // ④ ItemRequest 생성
        // status = REQUESTED 는 ItemRequest 생성자 내부에서 자동 세팅
        ItemRequest itemRequest = ItemRequest.builder()
                .requester(requester)
                .build();

        // ⑤ 양방향 연관관계 설정
        // addDetail() 내부에서 두 가지를 처리한다.
        //   - itemRequest.details 리스트에 detail 추가
        //   - detail.itemRequest 필드에 itemRequest 세팅 (package-private assignItemRequest 호출)
        // 이 메서드를 건너뛰고 직접 세팅하면 양방향 관계가 깨진다.
        itemRequest.addDetail(detail);

        // ⑥ 저장
        // cascade = ALL 이므로 ItemRequest 저장 시 ItemRequestDetail 도 함께 INSERT
        // ItemRequestDetailRepository.save() 별도 호출 불필요
        itemRequestRepository.save(itemRequest);
    }

    public List<ItemRequest> findAccessibleRequests(User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            return itemRequestRepository.findAll();
        }

        return itemRequestRepository.findByRequester(currentUser);
    }

    public ItemRequest findAccessibleRequest(Long requestId, User currentUser) {
        ItemRequest itemRequest = itemRequestRepository.findByIdWithDetails(requestId)
                .orElseThrow(ItemRequestNotFoundException::new);

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isRequester = itemRequest.getRequester().getId().equals(currentUser.getId());

        if (!isAdmin && !isRequester) {
            throw new UnauthorizedRequestAccessException();
        }

        return itemRequest;
    }


}
