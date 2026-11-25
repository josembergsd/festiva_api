package com.bradesco.festiva.application.port.in;

public interface AuthenticationUseCase {

    AccessToken register(String email, String password);

    AccessToken login(String email, String password);
}
