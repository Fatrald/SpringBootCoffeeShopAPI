package com.coffeeshop.orderservice.domain.exception;

public class InvalidOrderException extends OrderDomainException{
    public InvalidOrderException(String message){
        super(message);
    }
}
