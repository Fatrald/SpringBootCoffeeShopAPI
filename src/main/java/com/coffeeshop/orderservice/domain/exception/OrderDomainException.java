package com.coffeeshop.orderservice.domain.exception;

public abstract class OrderDomainException extends RuntimeException{
    protected OrderDomainException(String message){
        super(message);
    }
}
