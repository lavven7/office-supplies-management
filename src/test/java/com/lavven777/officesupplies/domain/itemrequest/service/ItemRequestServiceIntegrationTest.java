package com.lavven777.officesupplies.domain.itemrequest.service;

import com.lavven777.officesupplies.domain.inventory.entity.HistoryType;
import com.lavven777.officesupplies.domain.inventory.entity.InventoryHistory;
import com.lavven777.officesupplies.domain.inventory.repository.InventoryHistoryRepository;
import com.lavven777.officesupplies.domain.item.entity.Item;
import com.lavven777.officesupplies.domain.item.repository.ItemRepository;
import com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequest;
import com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequestDetail;
import com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequestStatus;
import com.lavven777.officesupplies.domain.itemrequest.repository.ItemRequestRepository;
import com.lavven777.officesupplies.domain.user.entity.User;
import com.lavven777.officesupplies.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryHistoryRepository inventoryHistoryRepository;

    @Autowired
    private EntityManager entityManager;

    private User requester;
    private User approver;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        requester = userRepository.save(
                createUser(
                        "EMP001",
                        "요청자",
                        "requester@test.com"
                )
        );

        approver = userRepository.save(
                createUser(
                        "EMP002",
                        "승인자",
                        "approver@test.com"
                )
        );

        item = itemRepository.save(
                Item.builder()
                        .name("A4 용지")
                        .description("테스트 비품")
                        .category("소모품")
                        .unitPrice(new BigDecimal("5000"))
                        .currentStock(100)
                        .minimumStock(10)
                        .build()
        );

        itemRequest = ItemRequest.builder()
                .requester(requester)
                .build();

        ItemRequestDetail detail = ItemRequestDetail.builder()
                .item(item)
                .quantity(30)
                .build();

        itemRequest.addDetail(detail);

        itemRequest = itemRequestRepository.save(itemRequest);
    }

    @Test
    void 요청을_승인하면_상태가_변경되고_재고가_차감되며_OUTBOUND_이력이_저장된다() {
        // given
        Long requestId = itemRequest.getId();
        Long approverId = approver.getId();
        Long itemId = item.getId();

        entityManager.flush();
        entityManager.clear();

        // when
        itemRequestService.approveRequest(requestId, approverId);

        entityManager.flush();
        entityManager.clear();

        // then
        ItemRequest foundRequest = itemRequestRepository.findById(requestId)
                .orElseThrow();

        Item foundItem = itemRepository.findById(itemId)
                .orElseThrow();

        List<InventoryHistory> histories =
                inventoryHistoryRepository
                        .findByItemOrderByCreatedAtDesc(foundItem);

        assertThat(foundRequest.getStatus())
                .isEqualTo(ItemRequestStatus.APPROVED);

        assertThat(foundRequest.getApprover().getId())
                .isEqualTo(approverId);

        assertThat(foundRequest.getApprovalDate())
                .isNotNull();

        assertThat(foundItem.getCurrentStock())
                .isEqualTo(70);

        assertThat(histories)
                .hasSize(1);

        InventoryHistory history = histories.get(0);

        assertThat(history.getHistoryType())
                .isEqualTo(HistoryType.OUTBOUND);

        assertThat(history.getQuantity())
                .isEqualTo(30);

        assertThat(history.getBeforeStock())
                .isEqualTo(100);

        assertThat(history.getAfterStock())
                .isEqualTo(70);

        assertThat(history.getCreatedBy().getId())
                .isEqualTo(approverId);
    }

    private User createUser(
            String employeeNumber,
            String name,
            String email
    ) {
        return User.builder()
                .employeeNumber(employeeNumber)
                .name(name)
                .email(email)
                .password("encoded-password")
                .department("개발팀")
                .position("사원")
                .build();
    }
}