package com.example.rachelklein.userauth.service;

import com.example.rachelklein.userauth.dto.request.RegisterRequest;
import com.example.rachelklein.userauth.dto.response.RegisterResponse;
import com.example.rachelklein.userauth.entity.User;
import com.example.rachelklein.userauth.repository.RefreshTokenRepository;
import com.example.rachelklein.userauth.repository.UserRepository;
import com.example.rachelklein.userauth.security.JwtService;
import com.example.rachelklein.userauth.util.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceRegisterTest {

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
    void register_success_shouldHashPasswordGenerateTokenSaveUserAndReturnResponse() {
        // arrange
        RegisterRequest req = new RegisterRequest();
        req.setEmail("user@example.com");
        req.setPassword("SecurePass123");

        RegisterRequest.Profile profile = new RegisterRequest.Profile();
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setBirthDate("1990-01-15");
        profile.setPhoneNumber("+972501234567");
        req.setProfile(profile);

        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123")).thenReturn("HASHED_PASSWORD");
        when(tokenService.generateToken()).thenReturn("VERIFICATION_TOKEN");

        // נוודא שה-save מחזיר User עם uid (כמו שקורה בפועל אחרי persist)
        UUID generatedUid = UUID.randomUUID();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUid(generatedUid);
            return u;
        });

        // act
        RegisterResponse res = accountService.register(req);

        // assert - response
        assertNotNull(res);
        assertEquals(generatedUid, res.getUid());
        assertEquals(200, res.getStatusCode());
        assertEquals("OK", res.getStatusMessage());
        assertTrue(res.isEmailVerificationRequired());
        assertEquals("VERIFICATION_TOKEN", res.getVerificationToken());

        // assert - saved entity
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals("user@example.com", saved.getEmail());
        assertEquals("HASHED_PASSWORD", saved.getPasswordHash());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertNotNull(saved.getBirthDate());
        assertEquals("1990-01-15", saved.getBirthDate().toString());
        assertEquals("+972501234567", saved.getPhoneNumber());

        assertFalse(Boolean.TRUE.equals(saved.getIsVerified()));
        assertTrue(Boolean.TRUE.equals(saved.getIsActive()));

        assertNotNull(saved.getRegistrationDate());
        assertEquals("VERIFICATION_TOKEN", saved.getVerificationToken());
        assertNotNull(saved.getVerificationTokenExpiry());

        // verify interactions
        verify(userRepository).existsByEmail("user@example.com");
        verify(passwordEncoder).encode("SecurePass123");
        verify(tokenService).generateToken();
        verifyNoMoreInteractions(jwtService); // register לא אמור לייצר JWT
    }
}

