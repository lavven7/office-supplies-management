package com.lavven777.officesupplies.domain.user.entity;

import com.lavven777.officesupplies.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사번. 회사 내 고유 식별자.
     * unique = true — 중복 사번 방지.
     */
    @Column(unique = true, nullable = false, length = 20)
    private String employeeNumber;

    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 로그인 ID로 사용.
     * unique = true — 중복 이메일 방지.
     */
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    /**
     * BCrypt 암호화된 비밀번호 저장.
     * 평문 저장 금지 — SecurityConfig의 PasswordEncoder를 통해 암호화 후 저장.
     */
    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String department;

    @Column(length = 50)
    private String position;

    /**
     * 권한. EnumType.STRING으로 저장 — "USER" 또는 "ADMIN".
     * EnumType.ORDINAL 사용 시 Enum 순서 변경에 취약하므로 STRING 사용.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    @Builder
    public User(String employeeNumber, String name, String email,
                String password, String department, String position) {
        this.employeeNumber = employeeNumber;
        this.name = name;
        this.email = email;
        this.password = password;
        this.department = department;
        this.position = position;
        this.role = Role.USER; // 가입 시 항상 USER — ADMIN은 DB에서 직접 부여
    }

    // -----------------------------------------------------------------------
    // 비즈니스 메서드
    // -----------------------------------------------------------------------

    /**
     * 비밀번호 변경.
     * 암호화는 Service 레이어에서 처리 후 이 메서드 호출.
     */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    /**
     * 부서/직급 정보 수정.
     */
    public void updateProfile(String department, String position) {
        this.department = department;
        this.position = position;
    }
}
