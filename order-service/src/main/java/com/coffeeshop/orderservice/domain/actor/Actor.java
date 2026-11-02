package com.coffeeshop.orderservice.domain.actor;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * userId = claim "sub" dari JWT Keycloak (UUID).
 */
public record Actor(UUID userId, Set<Role> roles) {

    public Actor {
        Objects.requireNonNull(userId, "UserID wajib diisi");
        roles = Set.copyOf(roles);
    }

    public boolean has(Role role) {
        return roles.contains(role);
    }

    public boolean hasAny(Role... candidates) {
        for (Role candidate : candidates) {
            if (roles.contains(candidate)) {
                return true;
            }
        }
        return false;
    }
}
