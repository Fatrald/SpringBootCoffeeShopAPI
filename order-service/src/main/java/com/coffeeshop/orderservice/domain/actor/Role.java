package com.coffeeshop.orderservice.domain.actor;

import java.util.Arrays;
import java.util.Optional;

public enum Role {
    CUSTOMER("customer"),
    WAITER("waiter"),
    BARISTA("barista"),
    CHEF("chef"),
    ADMIN("admin");

    /// Nama realm role di Keycloak (claim `realm_access.roles`).
    private final String keycloakRole;

    Role(String keycloakRole) {
        this.keycloakRole = keycloakRole;
    }

    public String keycloakRole() {
        return keycloakRole;
    }

    /// Role bawaan Keycloak yang tidak dikenal (misalnya `offline_access`)
    /// menghasilkan `Optional.empty()` dan cukup diabaikan.
    public static Optional<Role> fromKeycloakRole(String name) {
        return Arrays.stream(values())
                .filter(role -> role.keycloakRole.equals(name))
                .findFirst();
    }
}
