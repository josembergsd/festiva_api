package com.bradesco.festiva.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    OpenAPI festivaOpenApi() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(
                        "bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("Festiva Event API")
                        .version("1.0.0")
                        .description("""
                                API REST para cadastro e gerenciamento de eventos.

                                As consultas de eventos são públicas. Para criar, atualizar ou cancelar um evento,
                                registre-se ou autentique-se e informe o token retornado no botão Authorize.
                                """))
                .tags(List.of(
                        new Tag().name("Autenticação").description("Registro e autenticação de usuários."),
                        new Tag().name("Eventos").description("Consulta pública e gerenciamento de eventos.")));
    }
}
