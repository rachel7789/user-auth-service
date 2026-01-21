package com.example.rachelklein.userauth.exception;

public class ResetTokenExpiredException extends RuntimeException {
    public ResetTokenExpiredException() {
        super("Reset token expired");
    }
}
