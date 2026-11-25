package com.bradesco.festiva.application.service;

import com.bradesco.festiva.application.port.in.AccessToken;
import com.bradesco.festiva.application.port.in.AuthenticationUseCase;
import com.bradesco.festiva.application.port.out.UserRepository;
import com.bradesco.festiva.domain.model.EmailAlreadyRegisteredException;
import com.bradesco.festiva.domain.model.InvalidCredentialsException;
import com.bradesco.festiva.domain.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenIssuer tokenIssuer;
    private final Clock clock;

    @Override
    public AccessToken register(String email, String password) {
        String normalizedEmail = normalize(email);
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new EmailAlreadyRegisteredException();
        }

        UserAccount account = new UserAccount(
                UUID.randomUUID(),
                normalizedEmail,
                passwordEncoder.encode(password),
                clock.instant());
        try {
            userRepository.save(account);
        } catch (DuplicateKeyException exception) {
            throw new EmailAlreadyRegisteredException();
        }
        return tokenIssuer.issue(account);
    }

    @Override
    public AccessToken login(String email, String password) {
        UserAccount account = userRepository.findByEmail(normalize(email))
                .filter(user -> passwordEncoder.matches(password, user.passwordHash()))
                .orElseThrow(InvalidCredentialsException::new);
        return tokenIssuer.issue(account);
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
