package com.coffeeshop.orderservice;

import com.coffeeshop.orderservice.domain.common.OrderStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {
    @ParameterizedTest(name = "{0} -> {1} = {2}")
    @CsvSource({
            "CREATED,        IN_PREPARATION, true",
            "CREATED,        CANCELLED,      true",
            "CREATED,        READY,          false",
            "CREATED,        COMPLETED,      false",
            "IN_PREPARATION, READY,          true",
            "IN_PREPARATION, CANCELLED,      false",
            "READY,          COMPLETED,      true",
            "READY,          CANCELLED,      false",
            "COMPLETED,      CANCELLED,      false",
            "CANCELLED,      CREATED,        false"
    })
    void transition_rules(OrderStatus from, OrderStatus to, boolean expected) {
        assertThat(from.canTransitionTo(to)).isEqualTo(expected);
    }
}
