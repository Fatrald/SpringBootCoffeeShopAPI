package com.coffeeshop.orderservice.order.exception;

public class InvalidOrderException extends OrderDomainException{
    public InvalidOrderException(String message){
        super(message);
    }
}
