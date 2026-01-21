package com.example.rachelklein.userauth.exception;

public class InvalidVerificationTokenException extends RuntimeException {
    public InvalidVerificationTokenException() {
        super("Invalid token");
    }
}
