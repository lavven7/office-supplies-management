package com.lavven777.officesupplies.domain.item.repository;

import com.lavven777.officesupplies.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByActiveTrue();
}