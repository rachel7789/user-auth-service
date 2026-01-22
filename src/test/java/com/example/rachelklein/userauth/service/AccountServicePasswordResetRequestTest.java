package com.example.rachelklein.userauth.service;

import com.example.rachelklein.userauth.dto.request.PasswordResetRequest;
import com.example.rachelklein.userauth.entity.User;
import com.example.rachelklein.userauth.repository.RefreshTokenRepository;
import com.example.rachelklein.userauth.repository.UserRepository;
import com.example.rachelklein.userauth.security.JwtService;
import com.example.rachelklein.userauth.util.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServicePasswordResetRequestTest {

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

        accountService = new AccountService(
                userRepository,
                refreshTokenRepository,
                passwordEncoder,
                tokenService,
                jwtService
        );
    }

    @Test
    void requestPasswordReset_shouldGenerateTokenSetExpiryAndSaveUser() {
        // arrange
        String email = "reset.user@example.com";

        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(tokenService.generateToken()).thenReturn("RESET_TOKEN_123");

        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail(email);

        LocalDateTime before = LocalDateTime.now();

        // act
        accountService.requestPasswordReset(request);

        // assert
        assertNotNull(user.getPasswordResetToken(), "Password reset token should be set");
        assertEquals("RESET_TOKEN_123", user.getPasswordResetToken());

        assertNotNull(user.getPasswordResetTokenExpiry(), "Password reset token expiry should be set");
        assertTrue(
                user.getPasswordResetTokenExpiry().isAfter(before),
                "Expiry should be set to a future time"
        );

        verify(userRepository).findByEmail(email);
        verify(tokenService).generateToken();
        verify(userRepository).save(user);

        verifyNoInteractions(passwordEncoder, jwtService, refreshTokenRepository);
    }
}
