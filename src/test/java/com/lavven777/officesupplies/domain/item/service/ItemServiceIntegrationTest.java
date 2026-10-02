package com.lavven777.officesupplies.domain.item.service;

import com.lavven777.officesupplies.domain.inventory.entity.HistoryType;
import com.lavven777.officesupplies.domain.inventory.entity.InventoryHistory;
import com.lavven777.officesupplies.domain.inventory.repository.InventoryHistoryRepository;
import com.lavven777.officesupplies.domain.item.entity.Item;
import com.lavven777.officesupplies.domain.item.repository.ItemRepository;
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
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private InventoryHistoryRepository inventoryHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private Item item;
    private User createdBy;

    @BeforeEach
    void setUp() {
        createdBy = userRepository.save(
                User.builder()
                        .employeeNumber("EMP001")
                        .name("입고 담당자")
                        .email("manager@test.com")
                        .password("encoded-password")
                        .department("관리팀")
                        .position("사원")
                        .build()
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
    }

    @Test
    void 재고를_입고하면_재고가_증가하고_INBOUND_이력이_저장된다() {
        // given
        Long itemId = item.getId();
        Long createdById = createdBy.getId();
        int quantity = 30;

        // when
        itemService.inboundStock(itemId, quantity, createdById);

        entityManager.flush();
        entityManager.clear();

        // then
        Item foundItem = itemRepository.findById(itemId)
                .orElseThrow();

        List<InventoryHistory> histories =
                inventoryHistoryRepository
                        .findByItemOrderByCreatedAtDesc(foundItem);

        assertThat(foundItem.getCurrentStock()).isEqualTo(130);

        assertThat(histories).hasSize(1);

        InventoryHistory history = histories.get(0);

        assertThat(history.getHistoryType())
                .isEqualTo(HistoryType.INBOUND);

        assertThat(history.getQuantity())
                .isEqualTo(30);

        assertThat(history.getBeforeStock())
                .isEqualTo(100);

        assertThat(history.getAfterStock())
                .isEqualTo(130);

        assertThat(history.getItem().getId())
                .isEqualTo(itemId);

        assertThat(history.getCreatedBy().getId())
                .isEqualTo(createdById);
    }
}