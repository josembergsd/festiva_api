package com.bradesco.festiva.infrastructure.config;

import com.bradesco.festiva.application.service.TokenIssuer;
import com.bradesco.festiva.domain.model.UserAccount;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenIssuerTest {

    @Test
    void issuesSignedTokenWithUserAndExpirationClaims() {
        SecurityConfiguration configuration = new SecurityConfiguration();
        SecretKey key = configuration.jwtSecretKey(
                Base64.getEncoder().encodeToString("unit-test-signing-key-with-32-bytes".getBytes()));
        JwtEncoder encoder = configuration.jwtEncoder(key);
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        Instant now = Instant.parse("2026-01-01T10:00:00Z");
        JwtTimestampValidator timestampValidator = new JwtTimestampValidator();
        timestampValidator.setClock(Clock.fixed(now, ZoneOffset.UTC));
        decoder.setJwtValidator(timestampValidator);
        TokenIssuer issuer = new TokenIssuer(
                encoder,
                Clock.fixed(now, ZoneOffset.UTC),
                Duration.ofMinutes(30));
        UserAccount user = new UserAccount(
                UUID.fromString("6c2b64aa-b6e8-47e0-8cbd-d270d09737ab"),
                "person@example.com",
                "password-hash",
                now);

        Jwt decoded = decoder.decode(issuer.issue(user).value());

        assertEquals("person@example.com", decoded.getSubject());
        assertEquals(user.id().toString(), decoded.getClaimAsString("uid"));
        assertEquals(now.plus(Duration.ofMinutes(30)), decoded.getExpiresAt());
    }

    @Test
    void rejectsSigningSecretsShorterThan32Bytes() {
        SecurityConfiguration configuration = new SecurityConfiguration();
        String shortSecret = Base64.getEncoder().encodeToString("short".getBytes());

        assertThrows(IllegalStateException.class, () -> configuration.jwtSecretKey(shortSecret));
    }
}
