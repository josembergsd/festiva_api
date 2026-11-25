package com.bradesco.festiva.application.service;

import com.bradesco.festiva.application.port.in.AccessToken;
import com.bradesco.festiva.application.port.out.UserRepository;
import com.bradesco.festiva.domain.model.EmailAlreadyRegisteredException;
import com.bradesco.festiva.domain.model.InvalidCredentialsException;
import com.bradesco.festiva.domain.model.UserAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenIssuer tokenIssuer;

    @Mock
    private Clock clock;

    @InjectMocks
    private AuthenticationService service;

    @Test
    void registerNormalizesEmailAndStoresOnlyPasswordHash() {
        Instant now = Instant.parse("2026-01-01T10:00:00Z");
        AuthenticationService fixedClockService = new AuthenticationService(
                userRepository,
                passwordEncoder,
                tokenIssuer,
                Clock.fixed(now, ZoneOffset.UTC));
        when(userRepository.findByEmail("person@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("VeryStrongPassword#2026")).thenReturn("bcrypt-hash");
        when(userRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenIssuer.issue(any(UserAccount.class))).thenReturn(new AccessToken("token", 3600));

        AccessToken token = fixedClockService.register(" Person@Example.com ", "VeryStrongPassword#2026");

        assertEquals("token", token.value());
        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(account ->
                account.email().equals("person@example.com")
                        && account.passwordHash().equals("bcrypt-hash")
                        && account.createdAt().equals(now)));
    }

    @Test
    void registerReportsDuplicateEmail() {
        when(userRepository.findByEmail("person@example.com"))
                .thenReturn(Optional.of(new UserAccount(
                        UUID.randomUUID(),
                        "person@example.com",
                        "hash",
                        Instant.EPOCH)));

        assertThrows(EmailAlreadyRegisteredException.class,
                () -> service.register("person@example.com", "VeryStrongPassword#2026"));
    }

    @Test
    void registerTranslatesConcurrentUniqueConstraintConflict() {
        when(clock.instant()).thenReturn(Instant.EPOCH);
        when(userRepository.findByEmail("person@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("VeryStrongPassword#2026")).thenReturn("hash");
        when(userRepository.save(any(UserAccount.class))).thenThrow(new DuplicateKeyException("duplicate"));

        assertThrows(EmailAlreadyRegisteredException.class,
                () -> service.register("person@example.com", "VeryStrongPassword#2026"));
    }

    @Test
    void loginDoesNotRevealWhetherEmailExistsOrPasswordWasWrong() {
        when(userRepository.findByEmail("person@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> service.login("person@example.com", "wrong-password"));
    }

    @Test
    void loginIssuesTokenForMatchingPassword() {
        UserAccount account = new UserAccount(
                UUID.randomUUID(),
                "person@example.com",
                "bcrypt-hash",
                Instant.EPOCH);
        AccessToken expectedToken = new AccessToken("token", 3600);
        when(userRepository.findByEmail("person@example.com")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("VeryStrongPassword#2026", "bcrypt-hash")).thenReturn(true);
        when(tokenIssuer.issue(account)).thenReturn(expectedToken);

        assertEquals(expectedToken, service.login("person@example.com", "VeryStrongPassword#2026"));
    }
}
