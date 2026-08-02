package com.lavven777.officesupplies.global.security;

import com.lavven777.officesupplies.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security 인증 객체.
 *
 * UserDetails 를 구현해 Spring Security 가 인증 정보를 보관한다.
 * User Entity 를 감싸서 id, name 등 추가 정보를 함께 보관한다.
 *
 * 나중에 Controller 에서 이렇게 꺼낸다.
 *   @AuthenticationPrincipal CustomUserDetails userDetails
 *   Long userId = userDetails.getUser().getId();
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * 권한 목록 반환.
     *
     * Spring Security 는 권한명에 "ROLE_" 접두사를 요구한다.
     * hasRole("ADMIN") 은 내부적으로 "ROLE_ADMIN" 과 비교한다.
     * user.getRole().name() → "USER" 또는 "ADMIN"
     * → "ROLE_USER" 또는 "ROLE_ADMIN" 으로 등록.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    /** BCrypt 암호화된 비밀번호 반환. Spring Security 가 검증에 사용한다. */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 로그인 ID 반환.
     * 이 프로젝트는 email 을 로그인 ID 로 사용한다.
     * SecurityConfig 의 usernameParameter("email") 과 맞아야 한다.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // MVP 단계: 아래 네 가지는 모두 true 고정.
    // 계정 잠금/만료 기능이 생기면 User Entity 에 필드 추가 후 수정.
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}
