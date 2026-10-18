package com.coffeeshop.orderservice.domain.exception;

import com.coffeeshop.orderservice.domain.common.OrderStatus;

import java.util.UUID;

public class InvalidOrderStateTransitionException extends OrderDomainException {
    private final UUID orderId;
    private final OrderStatus from;
    private final OrderStatus to;

    public InvalidOrderStateTransitionException(UUID orderId, OrderStatus from, OrderStatus to){
        super("Order %s tidak bisa berubah dari %s ke %s".formatted(orderId, from, to));
        this.orderId = orderId;
        this.from = from;
        this.to = to;
    }

    public UUID orderId() { return orderId; }

    public OrderStatus from() { return from; }

    public OrderStatus to() { return to; }
}
