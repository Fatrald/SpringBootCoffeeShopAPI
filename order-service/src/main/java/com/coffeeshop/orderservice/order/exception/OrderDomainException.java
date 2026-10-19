package com.coffeeshop.orderservice.order.exception;

public abstract class OrderDomainException extends RuntimeException{
    protected OrderDomainException(String message){
        super(message);
    }
}
