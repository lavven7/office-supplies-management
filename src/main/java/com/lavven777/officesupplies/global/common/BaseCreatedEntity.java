package com.lavven777.officesupplies.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 생성 시각만 추적하는 기반 클래스.
 * 생성 후 수정되지 않는 불변 이력 데이터(InventoryHistory)가 상속한다.
 * updatedAt을 의도적으로 제외해 이력 데이터의 불변성을 코드 수준에서 표현한다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseCreatedEntity {

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
}