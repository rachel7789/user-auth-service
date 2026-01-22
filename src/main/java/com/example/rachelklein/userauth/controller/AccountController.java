package com.example.rachelklein.userauth.controller;

import com.example.rachelklein.userauth.dto.request.*;
import com.example.rachelklein.userauth.dto.response.AccountInfoResponse;
import com.example.rachelklein.userauth.dto.response.LoginResponse;
import com.example.rachelklein.userauth.dto.response.RefreshTokenResponse;
import com.example.rachelklein.userauth.dto.response.RegisterResponse;
import com.example.rachelklein.userauth.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = accountService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = accountService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@Valid @RequestBody VerifyRequest request) {
        accountService.verifyEmail(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<AccountInfoResponse> info(Authentication authentication) {
        AccountInfoResponse response = accountService.getAccountInfoFromAuthentication(authentication);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/password/reset-request")
    public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        accountService.requestPasswordReset(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetConfirmRequest request) {
        accountService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<AccountInfoResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        AccountInfoResponse response = accountService.updateProfile(authentication, request);
        return ResponseEntity.ok(response);
    }

    @SecurityRequirements
    @PostMapping("/token/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = accountService.refreshSessionToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }


}

