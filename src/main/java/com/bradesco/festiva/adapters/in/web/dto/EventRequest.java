package com.bradesco.festiva.adapters.in.web.dto;

import com.bradesco.festiva.application.port.in.EventData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

@Schema(name = "EventRequest", description = "Dados necessários para criar ou atualizar um evento.")
public record EventRequest(
        @Schema(description = "Nome do evento", example = "Festival de música", maxLength = 160)
        @NotBlank @Size(max = 160) String title,
        @Schema(description = "Descrição do evento", example = "Programação e informações adicionais", maxLength = 5000)
        @Size(max = 5000) String description,
        @Schema(description = "Data e hora de início em ISO-8601 com offset", example = "2030-07-01T18:00:00-03:00")
        @NotNull OffsetDateTime startsAt,
        @Schema(description = "Data e hora de término, posterior ao início", example = "2030-07-01T23:00:00-03:00")
        @NotNull OffsetDateTime endsAt,
        @Schema(description = "Local do evento", example = "São Paulo", maxLength = 240)
        @NotBlank @Size(max = 240) String venue,
        @Schema(description = "Capacidade máxima, maior que zero", example = "500", minimum = "1")
        @NotNull @Min(1) Integer capacity) {

    public EventData toEventData() {
        return new EventData(
                title,
                description,
                startsAt.toInstant(),
                endsAt.toInstant(),
                venue,
                capacity);
    }
}
