package com.lavven777.officesupplies.domain.user.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 로그인 화면 Controller.
 *
 * GET /login 만 처리한다.
 * POST /login 은 Spring Security 가 직접 처리하므로
 * 이 Controller 에 POST 매핑을 추가하면 안 된다.
 */
@Controller
public class LoginController {

    /**
     * 로그인 페이지 렌더링.
     *
     * 이미 로그인한 사용자가 /login 에 접근하면
     * Spring Security 가 자동으로 defaultSuccessUrl 로 리다이렉트한다.
     */
    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/requests";
        }

        return "login";
    }
}
