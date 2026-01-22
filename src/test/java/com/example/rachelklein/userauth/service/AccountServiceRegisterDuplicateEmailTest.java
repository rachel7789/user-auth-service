package com.example.rachelklein.userauth.service;

import com.example.rachelklein.userauth.dto.request.RegisterRequest;
import com.example.rachelklein.userauth.exception.DuplicateEmailException;
import com.example.rachelklein.userauth.repository.RefreshTokenRepository;
import com.example.rachelklein.userauth.repository.UserRepository;
import com.example.rachelklein.userauth.security.JwtService;
import com.example.rachelklein.userauth.util.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AccountServiceRegisterDuplicateEmailTest {

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
    void register_existingEmail_shouldThrowDuplicateEmailException() {
        // arrange
        RegisterRequest req = new RegisterRequest();
        req.setEmail("user@example.com");
        req.setPassword("SecurePass123");

        RegisterRequest.Profile profile = new RegisterRequest.Profile();
        profile.setFirstName("John");
        profile.setLastName("Doe");
        req.setProfile(profile);

        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        // act + assert
        assertThrows(
                DuplicateEmailException.class,
                () -> accountService.register(req)
        );

        // Verify – do not proceed with any further action.
        verify(userRepository).existsByEmail("user@example.com");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder, tokenService, jwtService);
    }
}
