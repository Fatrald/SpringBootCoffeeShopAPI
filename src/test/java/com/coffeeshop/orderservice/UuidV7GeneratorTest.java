package com.coffeeshop.orderservice;

import com.coffeeshop.orderservice.infrastructure.id.UuidV7Generator;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UuidV7GeneratorTest {
    private final UuidV7Generator generator = new UuidV7Generator();

    @Test
    void generates_version_7() {
        assertThat(generator.newId().version()).isEqualTo(7);
    }

    @Test
    void ids_are_increasing() {
        UUID first = generator.newId();
        UUID second = generator.newId();

        assertThat(second.toString()).isGreaterThan(first.toString());
    }
}
