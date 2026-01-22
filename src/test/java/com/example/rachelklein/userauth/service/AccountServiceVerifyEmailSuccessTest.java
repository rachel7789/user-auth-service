package com.example.rachelklein.userauth.service;

import com.example.rachelklein.userauth.dto.request.VerifyRequest;
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

class AccountServiceVerifyEmailSuccessTest {

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
    void verifyEmail_validToken_shouldVerifyAndClearTokenFields() {
        // arrange
        String email = "user@example.com";
        String token = "TOKEN123";

        User user = new User();
        user.setEmail(email);
        user.setIsVerified(false);
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusMinutes(10));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VerifyRequest req = new VerifyRequest();
        req.setEmail(email);
        req.setToken(token);

        // act
        accountService.verifyEmail(req);

        // assert
        assertTrue(Boolean.TRUE.equals(user.getIsVerified()));
        assertNull(user.getVerificationToken());
        assertNull(user.getVerificationTokenExpiry());

        verify(userRepository).findByEmail(email);
        verify(userRepository).save(user);
        verifyNoInteractions(passwordEncoder, tokenService, jwtService, refreshTokenRepository);
    }
}
