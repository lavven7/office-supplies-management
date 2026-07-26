package com.lavven777.officesupplies.domain.itemrequest.controller;

import com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequest;
import com.lavven777.officesupplies.domain.itemrequest.service.ItemRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ItemRequest itemRequest = itemRequestService.findById(id);
        model.addAttribute("itemRequest", itemRequest);
        return "itemrequests/detail";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id){
        itemRequestService.approveRequest(id, 1L);
        return "redirect:/requests/" + id;
    }

    /**
     * 비품 요청 반려.
     * POST /requests/{id}/reject
     *
     * @RequestParam("rejectReason"):
     * form의 <input name="rejectReason"> 값을 String으로 받는다.
     * DTO 없이 String 하나만 받으므로 간단하게 처리 가능.
     *
     * approverId는 Security 미적용 MVP 단계에서 1L로 하드코딩.
     * Security 적용 후 교체 예정.
     */
    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam("rejectReason") String rejectReason) {
        itemRequestService.rejectRequest(id, 1L, rejectReason);
        return "redirect:/requests/" + id;
    }



}