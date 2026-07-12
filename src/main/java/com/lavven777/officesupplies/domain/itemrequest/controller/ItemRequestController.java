package com.lavven777.officesupplies.domain.itemrequest.controller;

import com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequest;
import com.lavven777.officesupplies.domain.itemrequest.service.ItemRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public String list(Model model) {
        List<ItemRequest> requests = itemRequestService.findAll();
        model.addAttribute("requests", requests);
        return "itemrequests/list";
    }
}