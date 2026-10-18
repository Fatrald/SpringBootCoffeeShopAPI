package com.coffeeshop.orderservice.domain.aggregate;

import com.coffeeshop.orderservice.domain.valueobject.Actor;
import com.coffeeshop.orderservice.domain.valueobject.OrderItem;
import com.coffeeshop.orderservice.domain.valueobject.OrderPolicy;
import com.coffeeshop.orderservice.domain.common.OrderStatus;
import com.coffeeshop.orderservice.domain.exception.InvalidOrderException;
import com.coffeeshop.orderservice.domain.exception.InvalidOrderStateTransitionException;
import com.coffeeshop.orderservice.domain.exception.OrderAccessDeniedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


public class Order {
    private final UUID id;
    private final UUID customerId;
    private final UUID createdBy;
    private final List<OrderItem> items;
    private OrderStatus orderStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    public Order(UUID id, UUID customerId, UUID createdBy, List<OrderItem> items, Instant now) {
        this.id = Objects.requireNonNull(id, "ID wajib diisi");
        this.customerId = customerId;
        this.createdBy = createdBy;
        this.items = List.copyOf(items);
        this.createdAt = now;
        this.updatedAt = now;
        this.orderStatus = OrderStatus.CREATED;
    }

    public static void requireItems(List<OrderItem> items){
        if (items == null || items.isEmpty()){
            throw new InvalidOrderException("Order minimal berisi 1 item");
        }
    }

    public static Order placeByCustomer(UUID id, Actor actor, List<OrderItem> items, Clock clock){
        if (!OrderPolicy.canPlaceForSelf(actor)){
            throw new OrderAccessDeniedException("membuat order");
        }

        requireItems(items);

        return new Order(id, actor.userId(), actor.userId(), items, clock.instant());
    }

    public static Order placeWalkIn(UUID id, Actor actor, List<OrderItem> items, Clock clock) {
        if (!OrderPolicy.canPlaceWalkIn(actor)) {
            throw new OrderAccessDeniedException("membuat order walk-in");
        }
        requireItems(items);

        return new Order(id, null, actor.userId(), items, clock.instant());
    }

    // =========================================================
    // Transisi oleh SISTEM (dipicu event dari preparation-service)
    // =========================================================

    public void startPreparation(Clock clock) {
        if (orderStatus == OrderStatus.IN_PREPARATION) {
            return;
        }
        transitionTo(OrderStatus.IN_PREPARATION, clock);
    }

    public void markReady(Clock clock) {
        if (orderStatus == OrderStatus.READY) {
            return;
        }
        transitionTo(OrderStatus.READY, clock);
    }

    // =========================================================
    // Transisi oleh MANUSIA
    // =========================================================

    public void complete(Actor actor, Clock clock) {
        if (!OrderPolicy.canComplete(actor)) {
            throw new OrderAccessDeniedException("menyerahkan order");
        }
        transitionTo(OrderStatus.COMPLETED, clock);
    }

    public void cancel(Actor actor, Clock clock) {
        if (!OrderPolicy.canCancel(actor, this)) {
            throw new OrderAccessDeniedException("membatalkan order");
        }
        transitionTo(OrderStatus.CANCELLED, clock);
    }

    private void transitionTo(OrderStatus target, Clock clock) {
        if (!orderStatus.canTransitionTo(target)) {
            throw new InvalidOrderStateTransitionException(id, orderStatus, target);
        }
        this.orderStatus = target;
        this.updatedAt = clock.instant();
    }

    // =========================================================
    // Query
    // =========================================================

    public boolean isOwnedBy(UUID userId) {
        return customerId != null && customerId.equals(userId);
    }

    public BigDecimal totalAmount() {
        return items.stream()
                .map(OrderItem::subTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID id()                   { return id; }
    public Optional<UUID> customerId() { return Optional.ofNullable(customerId); }
    public UUID createdBy()            { return createdBy; }
    public List<OrderItem> items()     { return items; }
    public OrderStatus status()        { return orderStatus; }
    public Instant createdAt()         { return createdAt; }
    public Instant updatedAt()         { return updatedAt; }
}
