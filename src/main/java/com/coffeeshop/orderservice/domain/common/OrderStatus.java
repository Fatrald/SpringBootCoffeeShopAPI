package com.coffeeshop.orderservice.domain.common;

public enum OrderStatus {
    CREATED,
    IN_PREPARATION,
    READY,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus target){
        return switch(this) {
            case CREATED -> target == IN_PREPARATION || target == CANCELLED;
            case IN_PREPARATION -> target == READY;
            case READY -> target == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }

    public boolean isFinal(){
        return this == COMPLETED || this == CANCELLED;
    }
}
