package com.bradesco.festiva.adapters.in.web;

import com.bradesco.festiva.adapters.in.web.dto.AuthRequest;
import com.bradesco.festiva.adapters.in.web.dto.TokenResponse;
import com.bradesco.festiva.application.port.in.AccessToken;
import com.bradesco.festiva.application.port.in.AuthenticationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação")
public class AuthenticationController {

    private final AuthenticationUseCase authentication;

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário", description = "Registra uma conta e retorna um token JWT. A senha deve conter de 12 a 128 caracteres.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conta criada e token emitido"),
            @ApiResponse(
                    responseCode = "400",
                    description = "E-mail ou senha inválidos",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail já cadastrado",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    })
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody AuthRequest request) {
        AccessToken token = authentication.register(request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(token));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Valida as credenciais e retorna um token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticação realizada"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato de solicitação inválido",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "E-mail ou senha incorretos",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    })
    public TokenResponse login(@Valid @RequestBody AuthRequest request) {
        return toResponse(authentication.login(request.email(), request.password()));
    }

    private TokenResponse toResponse(AccessToken token) {
        return new TokenResponse(token.value(), "Bearer", token.expiresInSeconds());
    }
}
