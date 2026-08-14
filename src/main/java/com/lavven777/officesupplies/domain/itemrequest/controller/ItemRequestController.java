package com.lavven777.officesupplies.domain.itemrequest.controller;

import com.lavven777.officesupplies.domain.item.service.ItemService;
import com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequest;
import com.lavven777.officesupplies.domain.itemrequest.service.ItemRequestService;
import com.lavven777.officesupplies.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final ItemService itemService;

    @GetMapping
    public String list(Model model) {
        List<ItemRequest> requests = itemRequestService.findAll();
        model.addAttribute("requests", requests);
        return "itemrequests/list";
    }

    /**
     * 비품 요청 작성 화면.
     * GET /requests/new
     *
     * 주의: /requests/{id} 보다 위에 선언해야 한다.
     * Spring MVC는 정적 경로("/new")를 변수 경로("/{id}")보다 우선 매핑하지만
     * 명시적으로 위에 두는 것이 혼란을 줄인다.
     *
     * findActiveItems(): active = true 인 비품만 드롭다운에 표시.
     * 비활성화 비품은 선택 불가.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("items", itemService.findActiveItems());
        return "itemrequests/form";
    }

    /**
     * 비품 요청 생성.
     * POST /requests
     *
     * requesterId: Security 미적용 MVP 단계에서 1L 하드코딩.
     * Security 적용 후 @AuthenticationPrincipal로 교체 예정.
     *
     * PRG 패턴: 생성 후 redirect 로 POST 중복 제출 방지.
     */
    @PostMapping
    public String create(@RequestParam Long itemId,
                         @RequestParam Integer quantity,
                         @AuthenticationPrincipal CustomUserDetails loginUser) {
        Long requesterId = loginUser.getUser().getId();

        itemRequestService.createRequest(requesterId, itemId, quantity);

        return "redirect:/requests";
    }


    /**
     * 요청 상세 조회.
     * GET /requests/{id}
     *
     * /new 보다 아래에 선언.
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ItemRequest itemRequest = itemRequestService.findById(id);
        model.addAttribute("itemRequest", itemRequest);
        return "itemrequests/detail";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id,
                          @AuthenticationPrincipal CustomUserDetails loginUser) {
        Long approverId = loginUser.getUser().getId();

        itemRequestService.approveRequest(id, approverId);

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
                         @RequestParam("rejectReason") String rejectReason,
                         @AuthenticationPrincipal CustomUserDetails loginUser) {
        Long approverId = loginUser.getUser().getId();

        itemRequestService.rejectRequest(id, approverId, rejectReason);

        return "redirect:/requests/" + id;
    }


}