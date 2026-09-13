package com.lavven777.officesupplies.domain.item.service;

import com.lavven777.officesupplies.domain.inventory.entity.HistoryType;
import com.lavven777.officesupplies.domain.inventory.entity.InventoryHistory;
import com.lavven777.officesupplies.domain.inventory.repository.InventoryHistoryRepository;
import com.lavven777.officesupplies.domain.item.entity.Item;
import com.lavven777.officesupplies.domain.item.repository.ItemRepository;
import com.lavven777.officesupplies.domain.user.repository.UserRepository;
import com.lavven777.officesupplies.global.exception.InvalidQuantityException;
import com.lavven777.officesupplies.global.exception.ItemNotFoundException;
import com.lavven777.officesupplies.global.exception.UserNotFoundException;
import com.lavven777.officesupplies.domain.user.entity.User;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final InventoryHistoryRepository inventoryHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public Item registerItem(Item item) {
        return itemRepository.save(item);
    }

    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new);
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    /**
     * 활성화된 비품 목록 조회.
     *
     * 비품 요청 작성 화면의 드롭다운에서 사용.
     * active = false 인 비품(비활성화된 비품)은 제외한다.
     * ItemRepository.findByActiveTrue() 를 호출한다.
     */
    public List<Item> findActiveItems() {
        return itemRepository.findByActiveTrue();
    }


    @Transactional
    public void deactivateItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new);

        item.deactivate();
    }

    @Transactional
    public void inboundStock(Long itemId, Integer quantity, Long createdById) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException();
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);

        User createdBy = userRepository.findById(createdById)
                .orElseThrow(UserNotFoundException::new);

        int beforeStock = item.getCurrentStock();

        item.increaseStock(quantity);

        if (quantity == 999) {
            throw new RuntimeException("입고 롤백 테스트");
        }

        int afterStock = item.getCurrentStock();

        InventoryHistory history = InventoryHistory.builder()
                .item(item)
                .historyType(HistoryType.INBOUND)
                .quantity(quantity)
                .beforeStock(beforeStock)
                .afterStock(afterStock)
                .createdBy(createdBy)
                .build();


        inventoryHistoryRepository.save(history);
    }

}