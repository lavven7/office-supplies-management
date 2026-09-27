package com.lavven777.officesupplies.domain.itemrequest.entity;

import com.lavven777.officesupplies.domain.item.entity.Item;
import com.lavven777.officesupplies.global.exception.InvalidQuantityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemRequestDetailTest {

    @Test
    void 정상_수량이면_요청_상세가_생성된다() {
        // given
        Item item = createItem();

        // when
        ItemRequestDetail detail = ItemRequestDetail.builder()
                .item(item)
                .quantity(3)
                .build();

        // then
        assertThat(detail.getItem()).isSameAs(item);
        assertThat(detail.getQuantity()).isEqualTo(3);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void 수량이_null이거나_0_이하면_예외가_발생한다(Integer quantity) {
        // given
        Item item = createItem();

        // when & then
        assertThatThrownBy(() -> ItemRequestDetail.builder()
                .item(item)
                .quantity(quantity)
                .build())
                .isInstanceOf(InvalidQuantityException.class);
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