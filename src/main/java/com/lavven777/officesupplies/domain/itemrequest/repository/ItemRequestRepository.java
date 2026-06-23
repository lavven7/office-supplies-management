package com.lavven777.officesupplies.domain.itemrequest.repository;

import com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequest;
import com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequestStatus;
import com.lavven777.officesupplies.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    /**
     * 특정 직원의 요청 목록 조회.
     * 기존 메서드 — 변경 없음.
     */
    List<ItemRequest> findByRequester(User requester);

    /**
     * 요청 단건 조회 — details와 item을 fetch join으로 함께 로딩.
     *
     * [왜 이 메서드가 필요한가 - N+1 문제]
     *
     * findById()만 사용하면 아래처럼 쿼리가 여러 번 실행된다.
     *
     *   쿼리 1: SELECT * FROM item_requests WHERE id = ?   → ItemRequest 1건
     *   쿼리 2: SELECT * FROM item_request_details WHERE item_request_id = ?  → details 로딩
     *   쿼리 3: SELECT * FROM items WHERE id = ?   → detail[0]의 item 로딩
     *   쿼리 4: SELECT * FROM items WHERE id = ?   → detail[1]의 item 로딩
     *   쿼리 5: SELECT * FROM items WHERE id = ?   → detail[2]의 item 로딩
     *   ...
     *
     * details가 N개면 총 2 + N개의 쿼리가 실행된다. 이것이 N+1 문제.
     *
     * JOIN FETCH를 사용하면 아래 쿼리 1번으로 모두 해결된다.
     *   SELECT ir, d, i
     *   FROM ItemRequest ir
     *   JOIN FETCH ir.details d
     *   JOIN FETCH d.item i
     *   WHERE ir.id = :id
     *
     * [DISTINCT를 쓰는 이유]
     * JOIN 결과는 details 수만큼 ItemRequest 행이 중복된다.
     * details가 3개면 같은 ItemRequest가 3번 반복된 결과가 나온다.
     * JPQL의 DISTINCT는 Java 객체 레벨에서 중복을 제거해 ItemRequest 1개만 반환한다.
     *
     * [Optional을 반환하는 이유]
     * 해당 id의 요청이 없을 수 있다.
     * 호출하는 쪽(Service)에서 .orElseThrow()로 예외를 명시적으로 처리한다.
     */
    @Query("SELECT DISTINCT ir FROM ItemRequest ir " +
            "JOIN FETCH ir.details d " +
            "JOIN FETCH d.item " +
            "WHERE ir.id = :id")
    Optional<ItemRequest> findByIdWithDetails(@Param("id") Long id);

    /**
     * 상태별 요청 건수 조회.
     * 관리자 대시보드의 "승인 대기 요청 수" 카드에서 사용.
     */
    long countByStatus(ItemRequestStatus status);
}
