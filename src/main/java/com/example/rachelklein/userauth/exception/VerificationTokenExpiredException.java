package com.example.rachelklein.userauth.exception;

public class VerificationTokenExpiredException extends RuntimeException {
    public VerificationTokenExpiredException() {
        super("Token expired");
    }
}
