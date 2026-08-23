package com.lavven777.officesupplies.domain.item.controller;

import com.lavven777.officesupplies.domain.inventory.entity.InventoryHistory;
import com.lavven777.officesupplies.domain.item.entity.Item;
import com.lavven777.officesupplies.domain.inventory.service.InventoryHistoryService;
import com.lavven777.officesupplies.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final InventoryHistoryService inventoryHistoryService;

    @GetMapping("/items")
    public String items(Model model) {
        model.addAttribute("items", itemService.findAll());
        return "items/list";
    }

    @GetMapping("/items/{id}")
    public String detail(@PathVariable Long id, Model model) {

        // 비품 기본 정보를 조회
        Item item = itemService.findById(id);


        // 해당 비품의 재고 이력을 최신순으로 조회
        List<InventoryHistory> histories =
                inventoryHistoryService.findByItem(item);

        model.addAttribute("item", item);
        model.addAttribute("histories", histories);

        return "items/detail";
    }
}
