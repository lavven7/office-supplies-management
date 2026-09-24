package com.lavven777.officesupplies.domain.item.entity;

import com.lavven777.officesupplies.global.exception.InsufficientStockException;
import com.lavven777.officesupplies.global.exception.InvalidQuantityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemTest {

    @Test
    void 재고를_차감한다() {
        // given
        Item item = createItem(10, 3);

        // when
        item.decreaseStock(4);

        // then
        assertThat(item.getCurrentStock()).isEqualTo(6);
    }

    @Test
    void 현재_재고보다_많이_차감하면_예외가_발생한다() {
        // given
        Item item = createItem(10, 3);

        // when & then
        assertThatThrownBy(() -> item.decreaseStock(11))
                .isInstanceOf(InsufficientStockException.class);

        assertThat(item.getCurrentStock()).isEqualTo(10);
    }

    @Test
    void 재고를_증가시킨다() {
        // given
        Item item = createItem(10, 3);

        // when
        item.increaseStock(5);

        // then
        assertThat(item.getCurrentStock()).isEqualTo(15);
    }


    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void 차감_수량은_1_이상이어야_한다(int quantity) {
        // given
        Item item = createItem(10, 3);

        // when & then
        assertThatThrownBy(() -> item.decreaseStock(quantity))
                .isInstanceOf(InvalidQuantityException.class);

        assertThat(item.getCurrentStock()).isEqualTo(10);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void 증가_수량은_1_이상이어야_한다(int quantity) {
        // given
        Item item = createItem(10, 3);

        // when & then
        assertThatThrownBy(() -> item.increaseStock(quantity))
                .isInstanceOf(InvalidQuantityException.class);

        assertThat(item.getCurrentStock()).isEqualTo(10);
    }


    @Test
    void 현재_재고가_최소_재고와_같으면_재고_부족이다() {
        // given
        Item item = createItem(10, 10);

        // when
        boolean lowStock = item.isLowStock();

        // then
        assertThat(lowStock).isTrue();
    }

    @Test
    void 현재_재고가_최소_재고보다_많으면_재고_부족이_아니다() {
        // given
        Item item = createItem(11, 10);

        // when
        boolean lowStock = item.isLowStock();

        // then
        assertThat(lowStock).isFalse();
    }

    @Test
    void 비품을_비활성화한다() {
        // given
        Item item = createItem(10, 3);

        assertThat(item.isActive()).isTrue();

        // when
        item.deactivate();

        // then
        assertThat(item.isActive()).isFalse();
    }

    @Test
    void 이미_비활성화된_비품을_다시_비활성화해도_상태가_유지된다() {
        // given
        Item item = createItem(10, 3);
        item.deactivate();

        // when
        item.deactivate();

        // then
        assertThat(item.isActive()).isFalse();
    }


    private Item createItem(Integer currentStock, Integer minimumStock) {
        return Item.builder()
                .name("테스트 비품")
                .description("테스트 설명")
                .category("사무용품")
                .currentStock(currentStock)
                .minimumStock(minimumStock)
                .build();
    }
}