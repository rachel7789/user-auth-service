package com.example.rachelklein.userauth.service;

import com.example.rachelklein.userauth.dto.request.*;
import com.example.rachelklein.userauth.dto.response.AccountInfoResponse;
import com.example.rachelklein.userauth.dto.response.LoginResponse;
import com.example.rachelklein.userauth.dto.response.RefreshTokenResponse;
import com.example.rachelklein.userauth.dto.response.RegisterResponse;
import com.example.rachelklein.userauth.entity.RefreshToken;
import com.example.rachelklein.userauth.entity.User;
import com.example.rachelklein.userauth.exception.*;
import com.example.rachelklein.userauth.repository.RefreshTokenRepository;
import com.example.rachelklein.userauth.repository.UserRepository;
import com.example.rachelklein.userauth.security.JwtService;
import com.example.rachelklein.userauth.util.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    public AccountService(UserRepository userRepository,RefreshTokenRepository refreshTokenRepository,
                          PasswordEncoder passwordEncoder, TokenService tokenService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.jwtService = jwtService;
    }

    public RegisterResponse register(RegisterRequest request) {

        // Check for existing email.
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already exists");
        }

        // DTO to Entity mapping.
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getProfile().getFirstName());
        user.setLastName(request.getProfile().getLastName());

        if (request.getProfile().getBirthDate() != null) {
            user.setBirthDate(LocalDate.parse(request.getProfile().getBirthDate()));
        }

        user.setPhoneNumber(request.getProfile().getPhoneNumber());
        user.setRegistrationDate(LocalDateTime.now());
        user.setIsVerified(false);
        user.setIsActive(true);

        // Create a temporary email verification token
        user.setVerificationToken(tokenService.generateToken());
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        // Build the response.
        RegisterResponse response = new RegisterResponse();
        response.setUid(user.getUid());
        response.setStatusCode(200);
        response.setStatusMessage("OK");
        response.setEmailVerificationRequired(true);
        response.setVerificationToken(user.getVerificationToken());

        return response;
    }

    public LoginResponse login(LoginRequest request) {

        // Retrieve by email (login ID).
        User user = userRepository.findByEmail(request.getLoginID()).orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        // Password validation.
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // Email verification check.
        if (!Boolean.TRUE.equals(user.getIsVerified())) {
            throw new AccountNotVerifiedException("Account not verified");
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new AccountInactiveException();
        }

        // Update lastLogin.
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);

        // Build the response (without sessionToken at this stage).
        LoginResponse response = new LoginResponse();
        response.setUid(
                user.getUid());
        response.setStatusCode(200);

        LoginResponse.Profile profile = new LoginResponse.Profile();
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setEmail(user.getEmail());
        profile.setVerified(user.getIsVerified());

        response.setProfile(profile);
        response.setSessionToken(jwtService.generateToken(user));

        // Refresh Token (for JWT refresh capability)
        refreshTokenRepository.deleteAllByUser(user);

        String refreshTokenValue = tokenService.generateToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);
        refreshToken.setExpiry(LocalDateTime.now().plusDays(7)); // 7 ימים (אפשר לשנות)
        refreshTokenRepository.save(refreshToken);

        response.setRefreshToken(refreshTokenValue);

        return response;
    }

    public void verifyEmail(VerifyRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException());

        if (user.getVerificationToken() == null || !user.getVerificationToken().equals(request.getToken())) {
            throw new InvalidVerificationTokenException();
        }

        if (user.getVerificationTokenExpiry() == null || user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new VerificationTokenExpiredException();
        }

        user.setIsVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);

        userRepository.save(user);
    }

    public void requestPasswordReset(PasswordResetRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(UserNotFoundException::new);

        String resetToken = tokenService.generateToken();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));

        userRepository.save(user);

        log.info("Password reset token for {}: {}", user.getEmail(), resetToken);
    }

    public void resetPassword(PasswordResetConfirmRequest request) {

        User user = userRepository.findByPasswordResetToken(request.getToken()).orElseThrow(InvalidResetTokenException::new);

        if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResetTokenExpiredException();
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);

        userRepository.save(user);
    }

    public AccountInfoResponse getAccountInfoFromAuthentication(Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new MissingAuthorizationHeaderException();
        }

        UUID uid = UUID.fromString(authentication.getPrincipal().toString());

        User user = userRepository.findById(uid)
                .orElseThrow(UserNotFoundException::new);

        return buildAccountInfoResponse(user);
    }

    public AccountInfoResponse updateProfile(Authentication authentication, UpdateProfileRequest request) {

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new MissingAuthorizationHeaderException();
        }

        UUID uid = UUID.fromString(authentication.getPrincipal().toString());

        User user = userRepository.findById(uid)
                .orElseThrow(UserNotFoundException::new);

        UpdateProfileRequest.Profile profile = request.getProfile();

        if (profile.getFirstName() != null) user.setFirstName(profile.getFirstName());
        if (profile.getLastName() != null) user.setLastName(profile.getLastName());
        if (profile.getPhoneNumber() != null) user.setPhoneNumber(profile.getPhoneNumber());
        if (profile.getBirthDate() != null) user.setBirthDate(profile.getBirthDate());

        userRepository.save(user);

        return buildAccountInfoResponse(user);
    }

    private AccountInfoResponse buildAccountInfoResponse(User user) {
        AccountInfoResponse response = new AccountInfoResponse();
        response.setUid(user.getUid());
        response.setVerified(Boolean.TRUE.equals(user.getIsVerified()));
        response.setRegistrationDate(user.getRegistrationDate());
        response.setLastLogin(user.getLastLoginDate());

        AccountInfoResponse.Profile p = new AccountInfoResponse.Profile();
        p.setFirstName(user.getFirstName());
        p.setLastName(user.getLastName());
        p.setEmail(user.getEmail());
        p.setBirthDate(user.getBirthDate() != null ? user.getBirthDate().toString() : null);
        p.setPhoneNumber(user.getPhoneNumber());

        response.setProfile(p);
        return response;
    }

    public RefreshTokenResponse refreshSessionToken(String refreshToken) {

        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(InvalidTokenException::new);

        if (stored.getExpiry().isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException();
        }

        User user = stored.getUser();

        // JWT חדש (access token חדש)
        String newJwt = jwtService.generateToken(user);

        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setSessionToken(newJwt);
        response.setStatusCode(200);

        return response;
    }



}
