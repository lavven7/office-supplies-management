package com.lavven777.officesupplies.config;

import com.lavven777.officesupplies.global.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정.
 *
 * Spring Boot 3 / Spring Security 6 기준.
 * WebSecurityConfigurerAdapter 는 삭제되었으므로 사용 불가.
 * SecurityFilterChain Bean 방식으로 작성.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ── 접근 권한 ──────────────────────────────────────────────────
                .authorizeHttpRequests(auth -> auth
                        // 로그인 페이지는 누구나 접근 가능
                        .requestMatchers("/login").permitAll()
                        // 정적 리소스 허용
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // 비품 등록 화면은 ADMIN만 접근 가능
                        .requestMatchers("/items/new").hasRole("ADMIN")

                        // 비품 등록 요청은 ADMIN만 가능
                        .requestMatchers(HttpMethod.POST, "/items").hasRole("ADMIN")

                        // anyRequest()보다 위에 있어야됨
                        // 요청 승인/반려는 ADMIN만 가능
                        .requestMatchers(
                                HttpMethod.POST,
                                "/requests/*/approve",
                                "/requests/*/reject"
                        ).hasRole("ADMIN")



                        // 나머지는 로그인 필요
                        .anyRequest().authenticated()
                )

                // ── 로그인 ──────────────────────────────────────────────────────
                .formLogin(form -> form
                        // 커스텀 로그인 페이지 (이 경로는 permitAll 이어야 함)
                        .loginPage("/login")
                        // form input name 과 일치해야 함
                        // CustomUserDetails.getUsername() 이 email 을 반환하는 것과 맞춰야 함
                        .usernameParameter("email")
                        .passwordParameter("password")
                        // POST /login 을 Spring Security 가 처리하는 URL
                        // LoginController 에 이 경로의 POST 매핑을 만들면 충돌하므로 만들지 말 것
                        .loginProcessingUrl("/login")
                        // 로그인 성공 후 이동 URL
                        // alwaysUse = true: 이전 요청 URL 무시하고 항상 /requests 로
                        .defaultSuccessUrl("/requests", true)
                        // 로그인 실패 시 이동 URL
                        .failureUrl("/login?error=true")
                )

                // ── 로그아웃 ────────────────────────────────────────────────────
                .logout(logout -> logout
                        // POST /logout 을 Spring Security 가 처리
                        // Thymeleaf form 에서 th:action="@{/logout}" method="post" 로 호출
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        // 세션 무효화
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )

                // ── UserDetailsService 연결 ──────────────────────────────────────
                .userDetailsService(customUserDetailsService);

        // CSRF 는 기본 활성화 상태 유지
        // form 에 th:action 또는 CSRF hidden input 이 있어야 POST 요청이 허용됨

        return http.build();
    }

    /**
     * BCrypt 비밀번호 인코더 Bean 등록.
     *
     * 두 곳에서 사용된다.
     *   1. 로그인 시 입력 비밀번호와 DB BCrypt 해시 비교 (Spring Security 자동 처리)
     *   2. 나중에 회원가입 구현 시 UserService 에서 암호화
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
