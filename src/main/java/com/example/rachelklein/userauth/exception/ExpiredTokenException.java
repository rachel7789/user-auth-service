package com.example.rachelklein.userauth.exception;

public class ExpiredTokenException extends RuntimeException {

    public ExpiredTokenException() {
        super("Token has expired");
    }
}

