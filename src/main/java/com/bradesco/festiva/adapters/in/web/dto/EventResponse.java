package com.bradesco.festiva.adapters.in.web.dto;

import com.bradesco.festiva.domain.model.Event;
import com.bradesco.festiva.domain.model.EventStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "EventResponse", description = "Representação de um evento.")
public record EventResponse(
        @Schema(description = "Identificador do evento", example = "6c2b64aa-b6e8-47e0-8cbd-d270d09737ab")
        UUID id,
        @Schema(description = "Nome do evento", example = "Festival de música")
        String title,
        String description,
        @Schema(description = "Início em UTC")
        Instant startsAt,
        @Schema(description = "Término em UTC")
        Instant endsAt,
        @Schema(description = "Local", example = "São Paulo")
        String venue,
        @Schema(description = "Capacidade máxima", example = "500")
        int capacity,
        @Schema(description = "Status atual do evento", example = "SCHEDULED")
        EventStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.id(),
                event.title(),
                event.description(),
                event.startsAt(),
                event.endsAt(),
                event.venue(),
                event.capacity(),
                event.status(),
                event.createdAt(),
                event.updatedAt());
    }
}
