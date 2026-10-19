package com.coffeeshop.orderservice.domain;

import java.util.UUID;

@FunctionalInterface
public interface IdGenerator {
    UUID newId();
}
