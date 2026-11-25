package com.bradesco.festiva.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TokenResponse", description = "Token Bearer usado para autorizar operações protegidas.")
public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
}
