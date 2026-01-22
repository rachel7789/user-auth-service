package com.example.rachelklein.userauth.service;

import com.example.rachelklein.userauth.dto.request.PasswordResetConfirmRequest;
import com.example.rachelklein.userauth.entity.User;
import com.example.rachelklein.userauth.exception.ResetTokenExpiredException;
import com.example.rachelklein.userauth.repository.RefreshTokenRepository;
import com.example.rachelklein.userauth.repository.UserRepository;
import com.example.rachelklein.userauth.security.JwtService;
import com.example.rachelklein.userauth.util.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AccountServiceResetPasswordExpiredTokenTest {

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
    void resetPassword_expiredToken_shouldThrowResetTokenExpiredException() {
        // arrange
        PasswordResetConfirmRequest req = new PasswordResetConfirmRequest();
        req.setToken("RESET_TOKEN");
        req.setNewPassword("NewSecurePass456");

        User user = new User();
        user.setPasswordResetToken("RESET_TOKEN");
        user.setPasswordResetTokenExpiry(LocalDateTime.now().minusMinutes(1)); // פג תוקף

        when(userRepository.findByPasswordResetToken("RESET_TOKEN")).thenReturn(Optional.of(user));

        // act + assert
        assertThrows(ResetTokenExpiredException.class, () -> accountService.resetPassword(req));

        // verify - לא אמור לעדכן סיסמה ולא לשמור
        verify(userRepository).findByPasswordResetToken("RESET_TOKEN");
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(jwtService, tokenService);
    }
}
