package com.bradesco.festiva.application.port.in;

public record AccessToken(String value, long expiresInSeconds) {
}
