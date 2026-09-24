package com.lavven777.officesupplies.domain.itemrequest.entity;

import com.lavven777.officesupplies.domain.item.entity.Item;
import com.lavven777.officesupplies.domain.user.entity.User;
import com.lavven777.officesupplies.global.exception.InvalidRequestStatusException;
import org.junit.jupiter.api.Test;

import static com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequestStatus.APPROVED;
import static com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequestStatus.CANCELED;
import static com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequestStatus.REJECTED;
import static com.lavven777.officesupplies.domain.itemrequest.entity.ItemRequestStatus.REQUESTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemRequestTest {

    @Test
    void 요청을_생성하면_REQUESTED_상태이다() {
        // given
        User requester = createUser("EMP001", "요청자");

        // when
        ItemRequest itemRequest = ItemRequest.builder()
                .requester(requester)
                .build();

        // then
        assertThat(itemRequest.getRequester()).isSameAs(requester);
        assertThat(itemRequest.getStatus()).isEqualTo(REQUESTED);
        assertThat(itemRequest.getApprover()).isNull();
        assertThat(itemRequest.getApprovalDate()).isNull();
        assertThat(itemRequest.getRejectReason()).isNull();
    }

    @Test
    void 요청_상세를_추가하면_양방향_관계가_설정된다() {
        // given
        ItemRequest itemRequest = ItemRequest.builder()
                .requester(createUser("EMP001", "요청자"))
                .build();

        ItemRequestDetail detail = ItemRequestDetail.builder()
                .item(createItem())
                .quantity(3)
                .build();

        // when
        itemRequest.addDetail(detail);

        // then
        assertThat(itemRequest.getDetails())
                .hasSize(1)
                .containsExactly(detail);

        assertThat(detail.getItemRequest())
                .isSameAs(itemRequest);
    }

    @Test
    void 요청을_승인한다() {
        // given
        User requester = createUser("EMP001", "요청자");
        User approver = createUser("EMP002", "승인자");

        ItemRequest itemRequest = ItemRequest.builder()
                .requester(requester)
                .build();

        // when
        itemRequest.approve(approver);

        // then
        assertThat(itemRequest.getStatus()).isEqualTo(APPROVED);
        assertThat(itemRequest.getApprover()).isSameAs(approver);
        assertThat(itemRequest.getApprovalDate()).isNotNull();
        assertThat(itemRequest.getRejectReason()).isNull();
    }

    @Test
    void 요청을_반려한다() {
        // given
        User requester = createUser("EMP001", "요청자");
        User approver = createUser("EMP002", "반려자");

        ItemRequest itemRequest = ItemRequest.builder()
                .requester(requester)
                .build();

        // when
        itemRequest.reject(
                approver,
                "업무 목적이 불분명합니다."
        );

        // then
        assertThat(itemRequest.getStatus()).isEqualTo(REJECTED);
        assertThat(itemRequest.getApprover()).isSameAs(approver);
        assertThat(itemRequest.getRejectReason())
                .isEqualTo("업무 목적이 불분명합니다.");
        assertThat(itemRequest.getApprovalDate()).isNotNull();
    }

    @Test
    void 요청을_취소한다() {
        // given
        ItemRequest itemRequest = ItemRequest.builder()
                .requester(createUser("EMP001", "요청자"))
                .build();

        // when
        itemRequest.cancel();

        // then
        assertThat(itemRequest.getStatus()).isEqualTo(CANCELED);
        assertThat(itemRequest.getApprover()).isNull();
        assertThat(itemRequest.getApprovalDate()).isNull();
    }

    @Test
    void 이미_승인된_요청을_다시_승인하면_예외가_발생한다() {
        // given
        User requester = createUser("EMP001", "요청자");
        User approver = createUser("EMP002", "승인자");

        ItemRequest itemRequest = ItemRequest.builder()
                .requester(requester)
                .build();

        itemRequest.approve(approver);

        // when & then
        assertThatThrownBy(() ->
                itemRequest.approve(approver)
        ).isInstanceOf(
                InvalidRequestStatusException.class
        );

        assertThat(itemRequest.getStatus())
                .isEqualTo(APPROVED);
    }

    @Test
    void 승인된_요청을_반려하면_예외가_발생한다() {
        // given
        User requester = createUser("EMP001", "요청자");
        User approver = createUser("EMP002", "승인자");

        ItemRequest itemRequest = ItemRequest.builder()
                .requester(requester)
                .build();

        itemRequest.approve(approver);

        // when & then
        assertThatThrownBy(() ->
                itemRequest.reject(
                        approver,
                        "반려 사유"
                )
        ).isInstanceOf(
                InvalidRequestStatusException.class
        );

        assertThat(itemRequest.getStatus())
                .isEqualTo(APPROVED);
    }

    @Test
    void 승인된_요청을_취소하면_예외가_발생한다() {
        // given
        User requester = createUser("EMP001", "요청자");
        User approver = createUser("EMP002", "승인자");

        ItemRequest itemRequest = ItemRequest.builder()
                .requester(requester)
                .build();

        itemRequest.approve(approver);

        // when & then
        assertThatThrownBy(itemRequest::cancel)
                .isInstanceOf(
                        InvalidRequestStatusException.class
                );

        assertThat(itemRequest.getStatus())
                .isEqualTo(APPROVED);
    }

    private User createUser(
            String employeeNumber,
            String name
    ) {
        return User.builder()
                .employeeNumber(employeeNumber)
                .name(name)
                .email(
                        employeeNumber.toLowerCase()
                                + "@test.com"
                )
                .password("encoded-password")
                .department("개발팀")
                .position("사원")
                .build();
    }

    private Item createItem() {
        return Item.builder()
                .name("A4 용지")
                .description("테스트 비품")
                .category("사무용품")
                .currentStock(100)
                .minimumStock(10)
                .build();
    }
}