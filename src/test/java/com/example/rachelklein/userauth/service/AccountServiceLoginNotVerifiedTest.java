package com.example.rachelklein.userauth.service;

import com.example.rachelklein.userauth.dto.request.LoginRequest;
import com.example.rachelklein.userauth.entity.User;
import com.example.rachelklein.userauth.exception.AccountNotVerifiedException;
import com.example.rachelklein.userauth.repository.RefreshTokenRepository;
import com.example.rachelklein.userauth.repository.UserRepository;
import com.example.rachelklein.userauth.security.JwtService;
import com.example.rachelklein.userauth.util.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AccountServiceLoginNotVerifiedTest {

    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private PasswordEncoder passwordEncoder;
    private TokenService tokenService;
    private JwtService jwtService;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        tokenService = mock(TokenService.class);
        jwtService = mock(JwtService.class);

        accountService = new AccountService(userRepository, refreshTokenRepository, passwordEncoder, tokenService, jwtService);
    }

    @Test
    void login_userNotVerified_shouldThrowAccountNotVerifiedException() {
        // arrange
        LoginRequest req = new LoginRequest();
        req.setLoginID("user@example.com");
        req.setPassword("SecurePass123");

        User user = new User();
        user.setUid(UUID.randomUUID());
        user.setEmail("user@example.com");
        user.setPasswordHash("HASH");
        user.setIsVerified(false); // קריטי לטסט

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("SecurePass123", "HASH")).thenReturn(true);

        // act + assert
        assertThrows(AccountNotVerifiedException.class, () -> accountService.login(req));

        // verify
        verify(userRepository).findByEmail("user@example.com");
        verify(passwordEncoder).matches("SecurePass123", "HASH");
        verifyNoInteractions(jwtService); // לא אמור להנפיק JWT אם לא מאומת
        verify(userRepository, never()).save(any());
    }
}
