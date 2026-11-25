package com.bradesco.festiva.application.service;

import com.bradesco.festiva.application.port.in.AccessToken;
import com.bradesco.festiva.domain.model.UserAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Component
public class TokenIssuer {

    private final JwtEncoder jwtEncoder;
    private final Clock clock;
    private final Duration ttl;

    public TokenIssuer(
            JwtEncoder jwtEncoder,
            Clock clock,
            @Value("${app.security.jwt.ttl:PT1H}") Duration ttl) {
        this.jwtEncoder = jwtEncoder;
        this.clock = clock;
        this.ttl = ttl;
    }

    public AccessToken issue(UserAccount account) {
        Instant now = clock.instant();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("festiva-event-api")
                .subject(account.email())
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .claim("uid", account.id().toString())
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                claims)).getTokenValue();
        return new AccessToken(token, ttl.toSeconds());
    }
}
