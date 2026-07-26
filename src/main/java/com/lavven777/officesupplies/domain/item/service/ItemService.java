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

}