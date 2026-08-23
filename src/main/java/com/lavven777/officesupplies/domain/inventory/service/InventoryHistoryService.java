package com.lavven777.officesupplies.domain.inventory.service;


import com.lavven777.officesupplies.domain.inventory.entity.InventoryHistory;
import com.lavven777.officesupplies.domain.inventory.repository.InventoryHistoryRepository;
import com.lavven777.officesupplies.domain.item.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryHistoryService {

    private final InventoryHistoryRepository  inventoryHistoryRepository;

    public List<InventoryHistory> findByItem(Item item) {
        return inventoryHistoryRepository
                .findByItemOrderByCreatedAtDesc(item);
    }
}
