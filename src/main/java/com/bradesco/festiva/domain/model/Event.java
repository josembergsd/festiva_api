package com.bradesco.festiva.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Event(
        UUID id,
        String title,
        String description,
        Instant startsAt,
        Instant endsAt,
        String venue,
        int capacity,
        EventStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public Event {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(startsAt, "startsAt is required");
        Objects.requireNonNull(endsAt, "endsAt is required");
        Objects.requireNonNull(status, "status is required");
        Objects.requireNonNull(createdAt, "createdAt is required");
        Objects.requireNonNull(updatedAt, "updatedAt is required");
        title = normalizeText(title, "title", 160);
        venue = normalizeText(venue, "venue", 240);
        if (!endsAt.isAfter(startsAt)) {
            throw new InvalidEventException("endsAt must be after startsAt");
        }
        if (capacity < 1) {
            throw new InvalidEventException("capacity must be greater than zero");
        }
    }

    public static Event create(
            String title,
            String description,
            Instant startsAt,
            Instant endsAt,
            String venue,
            int capacity,
            Instant now) {
        return new Event(
                UUID.randomUUID(),
                title,
                description,
                startsAt,
                endsAt,
                venue,
                capacity,
                EventStatus.SCHEDULED,
                now,
                now);
    }

    public Event update(
            String newTitle,
            String newDescription,
            Instant newStartsAt,
            Instant newEndsAt,
            String newVenue,
            int newCapacity,
            Instant now) {
        return new Event(
                id,
                newTitle,
                newDescription,
                newStartsAt,
                newEndsAt,
                newVenue,
                newCapacity,
                status,
                createdAt,
                now);
    }

    public Event cancel(Instant now) {
        return new Event(
                id,
                title,
                description,
                startsAt,
                endsAt,
                venue,
                capacity,
                EventStatus.CANCELLED,
                createdAt,
                now);
    }

    private static String normalizeText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new InvalidEventException(field + " must not be blank");
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new InvalidEventException(field + " must contain at most " + maxLength + " characters");
        }
        return normalized;
    }
}
