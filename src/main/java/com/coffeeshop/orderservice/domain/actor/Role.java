package com.coffeeshop.orderservice.domain.actor;

import java.util.Arrays;
import java.util.Optional;

public enum Role {
    CUSTOMER("customer"),
    WAITER("waiter"),
    BARISTA("barista"),
    CHEF("chef"),
    ADMIN("admin");

    /// this is role from keycloak
    private final String keycloakRole;

    /// this is constructor for ROLE
    Role(String keycloakRole) {
        this.keycloakRole = keycloakRole;
    }

    public String getKeycloakRole(){
        return keycloakRole;
    }

    public static Optional<Role> fromKeycloakRole(String name){
        return Arrays.stream(values())
                .filter(role -> role.keycloakRole.equals(name))
                .findFirst();
    }
}
