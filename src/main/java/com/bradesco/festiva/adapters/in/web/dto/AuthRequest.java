package com.bradesco.festiva.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "AuthRequest", description = "Credenciais para registro ou autenticação.")
public record AuthRequest(
        @Schema(description = "Endereço de e-mail", example = "organizador@example.com", maxLength = 254)
        @NotBlank @Email @Size(max = 254) String email,
        @Schema(description = "Senha com pelo menos 12 caracteres", example = "SenhaSegura#2026", minLength = 12, maxLength = 128)
        @NotBlank @Size(min = 12, max = 128) String password) {
}
