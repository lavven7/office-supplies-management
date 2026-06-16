package com.lavven777.officesupplies.domain.inventory.repository;

import com.lavven777.officesupplies.domain.inventory.entity.InventoryHistory;
import com.lavven777.officesupplies.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {
    List<InventoryHistory> findByItemOrderByCreatedAtDesc(Item item);
}