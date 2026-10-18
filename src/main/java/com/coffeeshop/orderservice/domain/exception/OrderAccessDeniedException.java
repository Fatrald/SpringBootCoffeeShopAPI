package com.coffeeshop.orderservice.domain.exception;

public class OrderAccessDeniedException extends OrderDomainException {

    public OrderAccessDeniedException(String action) {
        super("Tidak diizinkan " + action);
    }
}
