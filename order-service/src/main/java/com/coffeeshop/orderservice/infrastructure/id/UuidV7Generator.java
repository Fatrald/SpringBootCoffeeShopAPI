package com.coffeeshop.orderservice.infrastructure.id;

import java.util.UUID;

import com.coffeeshop.orderservice.domain.IdGenerator;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

public class UuidV7Generator implements IdGenerator {

    private final TimeBasedEpochGenerator generator = Generators.timeBasedEpochGenerator();

    @Override
    public UUID newId() {
        return generator.generate();
    }
}
