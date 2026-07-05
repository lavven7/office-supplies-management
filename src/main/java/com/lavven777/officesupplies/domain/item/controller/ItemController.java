package com.lavven777.officesupplies.domain.item.controller;

import com.lavven777.officesupplies.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items")
    public String items(Model model) {
        model.addAttribute("items", itemService.findAll());
        return "items/list";
    }
}