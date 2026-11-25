package com.bradesco.festiva.application.port.in;

import java.time.Instant;

public record EventData(
        String title,
        String description,
        Instant startsAt,
        Instant endsAt,
        String venue,
        int capacity) {
}
