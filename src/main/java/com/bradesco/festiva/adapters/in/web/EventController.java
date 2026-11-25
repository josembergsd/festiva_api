package com.bradesco.festiva.adapters.in.web;

import com.bradesco.festiva.adapters.in.web.dto.EventPageResponse;
import com.bradesco.festiva.adapters.in.web.dto.EventRequest;
import com.bradesco.festiva.adapters.in.web.dto.EventResponse;
import com.bradesco.festiva.application.port.in.EventQuery;
import com.bradesco.festiva.application.port.in.ManageEventsUseCase;
import com.bradesco.festiva.domain.model.EventStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Validated
@Tag(name = "Eventos")
public class EventController {

    private final ManageEventsUseCase manageEvents;

    @GetMapping
    @Operation(
            summary = "Listar eventos",
            description = "Lista eventos com paginação opcional e filtro por status. A consulta é pública.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de eventos retornada"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetros inválidos",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    })
    public EventPageResponse list(
            @Parameter(description = "Número da página, começando em zero", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Quantidade de itens por página (máximo 100)", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @Parameter(description = "Filtra pelo status do evento")
            @RequestParam(required = false) EventStatus status) {
        return EventPageResponse.from(manageEvents.list(new EventQuery(page, size, status)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar evento", description = "Consulta um evento pelo identificador UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Evento não encontrado",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    })
    public EventResponse get(@PathVariable UUID id) {
        return EventResponse.from(manageEvents.get(id));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Criar evento", description = "Cria um evento. Requer token Bearer JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento criado"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou período incorreto",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
        EventResponse response = EventResponse.from(manageEvents.create(request.toEventData()));
        return ResponseEntity.created(URI.create("/api/v1/events/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualizar evento", description = "Substitui os dados editáveis do evento. Requer token Bearer JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento atualizado"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou período incorreto",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Evento não encontrado",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    })
    public EventResponse update(@PathVariable UUID id, @Valid @RequestBody EventRequest request) {
        return EventResponse.from(manageEvents.update(id, request.toEventData()));
    }

    @PatchMapping("/{id}/cancel")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cancelar evento", description = "Cancela o evento sem removê-lo do banco. Requer token Bearer JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento cancelado"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Evento não encontrado",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    })
    public EventResponse cancel(@PathVariable UUID id) {
        return EventResponse.from(manageEvents.cancel(id));
    }
}
