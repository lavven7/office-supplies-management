package com.lavven777.officesupplies.global.security;

import com.lavven777.officesupplies.domain.user.entity.User;
import com.lavven777.officesupplies.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security 인증 처리 서비스.
 *
 * POST /login 요청이 오면 Spring Security 가 자동으로
 * loadUserByUsername(email) 을 호출한다.
 * 개발자가 직접 호출하는 메서드가 아니다.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * email 로 사용자 조회 후 CustomUserDetails 로 감싸서 반환.
     *
     * @Transactional(readOnly = true):
     * 영속성 컨텍스트 안에서 user.getRole() 접근을 안전하게 처리.
     *
     * UsernameNotFoundException:
     * Spring Security 가 이 예외를 잡아 로그인 실패로 처리한다.
     * 직접 catch 할 필요 없다.
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("이메일을 찾을 수 없습니다: " + email));
        return new CustomUserDetails(user);
    }
}
