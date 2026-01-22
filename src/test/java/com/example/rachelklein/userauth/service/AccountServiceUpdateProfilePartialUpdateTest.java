package com.example.rachelklein.userauth.service;

import com.example.rachelklein.userauth.dto.request.UpdateProfileRequest;
import com.example.rachelklein.userauth.entity.User;
import com.example.rachelklein.userauth.repository.RefreshTokenRepository;
import com.example.rachelklein.userauth.repository.UserRepository;
import com.example.rachelklein.userauth.security.JwtService;
import com.example.rachelklein.userauth.util.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccountServiceUpdateProfilePartialUpdateTest {

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
    void updateProfile_shouldUpdateOnlyNonNullFields() {
        // arrange
        UUID uid = UUID.randomUUID();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(uid.toString());

        User user = new User();
        user.setUid(uid);
        user.setFirstName("OldFirst");
        user.setLastName("OldLast");
        user.setPhoneNumber("+972500000000");
        user.setBirthDate(LocalDate.of(1990, 1, 1));

        when(userRepository.findById(uid)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateProfileRequest req = new UpdateProfileRequest();
        UpdateProfileRequest.Profile profile = new UpdateProfileRequest.Profile();
        profile.setPhoneNumber("+972509999999"); // Only this is updated.
        req.setProfile(profile);

        // act
        accountService.updateProfile(auth, req);

        // assert
        assertEquals("OldFirst", user.getFirstName());
        assertEquals("OldLast", user.getLastName());
        assertEquals("+972509999999", user.getPhoneNumber());
        assertEquals(LocalDate.of(1990, 1, 1), user.getBirthDate());

        verify(userRepository).findById(uid);
        verify(userRepository).save(user);
        verifyNoInteractions(passwordEncoder, tokenService, jwtService, refreshTokenRepository);
    }
}
