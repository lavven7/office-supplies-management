package com.lavven777.officesupplies.domain.item.service;

import com.lavven777.officesupplies.domain.item.entity.Item;
import com.lavven777.officesupplies.domain.item.repository.ItemRepository;
import com.lavven777.officesupplies.global.exception.ItemNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

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
}