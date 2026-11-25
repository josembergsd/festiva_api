package com.bradesco.festiva.adapters.in.web;

import com.bradesco.festiva.adapters.in.web.dto.AuthRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostgresApiIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine");

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("app.security.jwt.secret-base64",
                () -> Base64.getEncoder().encodeToString("integration-test-key-at-least-32-bytes".getBytes()));
    }

    @Test
    void registersAuthenticatesProtectsWritesAndAllowsPublicReads() throws IOException, InterruptedException {
        String baseUrl = "http://localhost:" + port;
        AuthRequest credentials = new AuthRequest("organizer@example.com", "VeryStrongPassword#2026");
        HttpResponse<String> registration = send(
                baseUrl + "/api/v1/auth/register",
                "POST",
                credentialsJson(credentials),
                null);

        assertEquals(201, registration.statusCode());
        String token = extract(registration.body(), "accessToken");
        assertNotNull(token);

        String eventJson = """
                {
                  "title": "Festiva",
                  "description": "Annual gathering",
                  "startsAt": "2030-07-01T18:00:00-03:00",
                  "endsAt": "2030-07-01T23:00:00-03:00",
                  "venue": "Sao Paulo",
                  "capacity": 500
                }
                """;
        HttpResponse<String> unauthorized = send(
                baseUrl + "/api/v1/events",
                "POST",
                eventJson,
                null);
        assertEquals(401, unauthorized.statusCode());

        HttpResponse<String> created = send(baseUrl + "/api/v1/events", "POST", eventJson, token);
        assertEquals(201, created.statusCode());
        assertEquals("SCHEDULED", extract(created.body(), "status"));
        String eventId = extract(created.body(), "id");

        HttpResponse<String> publicRead = send(
                baseUrl + "/api/v1/events/" + eventId,
                "GET",
                null,
                null);
        assertEquals(200, publicRead.statusCode());
        assertEquals("Festiva", extract(publicRead.body(), "title"));

        HttpResponse<String> cancelled = send(
                baseUrl + "/api/v1/events/" + eventId + "/cancel",
                "PATCH",
                null,
                token);
        assertEquals(200, cancelled.statusCode());
        assertEquals("CANCELLED", extract(cancelled.body(), "status"));
    }

    @Test
    void exposesOpenApiDocumentAndSwaggerUi() throws IOException, InterruptedException {
        String baseUrl = "http://localhost:" + port;

        HttpResponse<String> openApi = send(baseUrl + "/v3/api-docs", "GET", null, null);
        assertEquals(200, openApi.statusCode());
        org.junit.jupiter.api.Assertions.assertAll(
                () -> org.junit.jupiter.api.Assertions.assertTrue(openApi.body().contains("Festiva Event API")),
                () -> org.junit.jupiter.api.Assertions.assertTrue(openApi.body().contains("/api/v1/events")),
                () -> org.junit.jupiter.api.Assertions.assertTrue(openApi.body().contains("/api/v1/auth/register")),
                () -> org.junit.jupiter.api.Assertions.assertTrue(openApi.body().contains("bearerAuth")));

        HttpResponse<String> swaggerUi = send(baseUrl + "/swagger-ui/index.html", "GET", null, null);
        assertEquals(200, swaggerUi.statusCode());
        org.junit.jupiter.api.Assertions.assertTrue(swaggerUi.body().contains("swagger-ui"));
    }

    private HttpResponse<String> send(String url, String method, String body, String token)
            throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(10));
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        if (body != null) {
            request.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(body));
        } else {
            request.method(method, HttpRequest.BodyPublishers.noBody());
        }
        return httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String credentialsJson(AuthRequest request) {
        return """
                {"email":"%s","password":"%s"}
                """.formatted(request.email(), request.password());
    }

    private String extract(String json, String property) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(property) + "\"\\s*:\\s*\"([^\"]+)\"")
                .matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }
}
