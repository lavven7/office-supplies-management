package com.lavven777.officesupplies.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화 설정.
 *
 * @EnableJpaAuditing 하나로 BaseTimeEntity, BaseCreatedEntity 모두 동작한다.
 * 두 클래스 모두 @EntityListeners(AuditingEntityListener.class)를 선언하고 있으므로
 * 별도 추가 설정 없이 createdAt, updatedAt이 자동으로 주입된다.
 *
 * 주의: @SpringBootApplication이 붙은 메인 클래스에 직접 선언하지 않고
 * 별도 Config 클래스로 분리한 이유 — @WebMvcTest 등 슬라이스 테스트 시
 * Auditing 설정이 로드되지 않아 발생하는 오류를 방지하기 위함.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
