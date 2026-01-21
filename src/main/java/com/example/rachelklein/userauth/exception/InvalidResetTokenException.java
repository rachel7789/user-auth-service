package com.example.rachelklein.userauth.exception;

public class InvalidResetTokenException extends RuntimeException {
    public InvalidResetTokenException() {
        super("Invalid reset token");
    }
}
