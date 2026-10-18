package com.coffeeshop.orderservice.domain.utils;

import java.util.UUID;

@FunctionalInterface
public interface IdGenerator {
    UUID newId();
}
