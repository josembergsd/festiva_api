package com.bradesco.festiva.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record UserAccount(UUID id, String email, String passwordHash, Instant createdAt) {

    public UserAccount {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(email, "email is required");
        Objects.requireNonNull(passwordHash, "passwordHash is required");
        Objects.requireNonNull(createdAt, "createdAt is required");
    }
}
