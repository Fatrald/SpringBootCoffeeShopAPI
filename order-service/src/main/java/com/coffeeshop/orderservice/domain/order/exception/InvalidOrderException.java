package com.coffeeshop.orderservice.domain.order.exception;

public class InvalidOrderException extends OrderDomainException{
    public InvalidOrderException(String message){
        super(message);
    }
}
