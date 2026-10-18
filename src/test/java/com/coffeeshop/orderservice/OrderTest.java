package com.coffeeshop.orderservice;

import com.coffeeshop.orderservice.domain.aggregate.Order;
import com.coffeeshop.orderservice.domain.common.OrderStatus;
import com.coffeeshop.orderservice.domain.common.Role;
import com.coffeeshop.orderservice.domain.common.Station;
import com.coffeeshop.orderservice.domain.exception.InvalidOrderException;
import com.coffeeshop.orderservice.domain.exception.OrderAccessDeniedException;
import com.coffeeshop.orderservice.domain.valueobject.Actor;
import com.coffeeshop.orderservice.domain.valueobject.OrderItem;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class OrderTest {
    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2026-10-18T08:00:00Z"), ZoneOffset.UTC);

    private static final Actor CUSTOMER_A = actorWith(Role.CUSTOMER);
    private static final Actor CUSTOMER_B = actorWith(Role.CUSTOMER);
    private static final Actor WAITER     = actorWith(Role.WAITER);
    private static final Actor BARISTA    = actorWith(Role.BARISTA);
    private static final Actor ADMIN      = actorWith(Role.ADMIN);

    // ---------------------------------------------------------
    // Helper
    // ---------------------------------------------------------

    private static Actor actorWith(Role role){
        return new Actor(UUID.randomUUID(), Set.of(role));
    }

    private static List<OrderItem> sampleItems() {
        return List.of(
                new OrderItem(UUID.randomUUID(), "Caffe Latte", 2, new BigDecimal("35000.00"), Station.BAR),
                new OrderItem(UUID.randomUUID(), "Croissant",   1, new BigDecimal("25000.00"), Station.KITCHEN)
        );
    }

    private static Order newCustomOrder(){
        return Order.placeByCustomer(UUID.randomUUID(), CUSTOMER_A, sampleItems(), CLOCK);
    }

    private static Order newWalkInOrder(){
        return Order.placeWalkIn(UUID.randomUUID(), WAITER, sampleItems(), CLOCK);
    }

    // ---------------------------------------------------------
    // Membuat order
    // ---------------------------------------------------------

    @Nested
    class Place {

        @Test
        void customer_order_is_owned_by_that_customer(){
            Order order = newCustomOrder();
            assertThat(order.status()).isEqualTo(OrderStatus.CREATED);
            assertThat(order.isOwnedBy(CUSTOMER_A.userId())).isTrue();
            assertThat(order.createdBy()).isEqualTo(CUSTOMER_A.userId());
            assertThat(order.totalAmount()).isEqualByComparingTo("95000.00");
            assertThat(order.createdAt()).isEqualTo(CLOCK.instant());
        }

        @Test
        void walk_in_order_has_no_customer(){
            Order order = newWalkInOrder();

            assertThat(order.customerId()).isEmpty();
            assertThat(order.createdBy()).isEqualTo(WAITER.userId());
        }

        @Test
        void walk_in_order_is_not_owned_by_anyone() {
            Order order = newWalkInOrder();

            assertThat(order.isOwnedBy(CUSTOMER_A.userId())).isFalse();
            assertThat(order.isOwnedBy(null)).isFalse();
        }

        @Test
        void order_without_items_is_rejected() {
            assertThatThrownBy(() -> Order.placeByCustomer(UUID.randomUUID(), CUSTOMER_A, List.of(), CLOCK))
                    .isInstanceOf(InvalidOrderException.class);
        }

        @Test
        void barista_cannot_place_order() {
            assertThatThrownBy(() -> Order.placeByCustomer(UUID.randomUUID(), BARISTA, sampleItems(), CLOCK))
                    .isInstanceOf(OrderAccessDeniedException.class);
        }

        @Test
        void customer_cannot_place_walk_in_order() {
            assertThatThrownBy(() -> Order.placeWalkIn(UUID.randomUUID(), CUSTOMER_A, sampleItems(), CLOCK))
                    .isInstanceOf(OrderAccessDeniedException.class);
        }
    }

    // ---------------------------------------------------------
    // Membatalkan order
    // ---------------------------------------------------------

    @Nested
    class Cancel {

        @Test
        void owner_can_cancel_created_order() {
            Order order = newCustomerOrder();

            order.cancel(CUSTOMER_A, CLOCK);

            assertThat(order.status()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        void admin_can_cancel_any_created_order() {
            Order order = newCustomerOrder();

            order.cancel(ADMIN, CLOCK);

            assertThat(order.status()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        void other_customer_cannot_cancel() {
            Order order = newCustomerOrder();

            assertThatThrownBy(() -> order.cancel(CUSTOMER_B, CLOCK))
                    .isInstanceOf(OrderAccessDeniedException.class);

            assertThat(order.status()).isEqualTo(OrderStatus.CREATED);
        }

        @Test
        void cannot_cancel_after_preparation_started() {
            Order order = newCustomerOrder();
            order.startPreparation(CLOCK);

            assertThatThrownBy(() -> order.cancel(CUSTOMER_A, CLOCK))
                    .isInstanceOf(InvalidOrderStateTransitionException.class);
        }
    }
}
