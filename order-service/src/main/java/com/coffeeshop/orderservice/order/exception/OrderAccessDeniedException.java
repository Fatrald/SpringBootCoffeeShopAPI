package com.coffeeshop.orderservice.order.exception;

public class OrderAccessDeniedException extends OrderDomainException {

    public OrderAccessDeniedException(String action) {
        super("Tidak diizinkan " + action);
    }
}
