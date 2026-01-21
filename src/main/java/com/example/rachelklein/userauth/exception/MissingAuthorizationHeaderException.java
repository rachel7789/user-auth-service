package com.example.rachelklein.userauth.exception;

public class MissingAuthorizationHeaderException extends RuntimeException {
    public MissingAuthorizationHeaderException() {
        super("Missing Authorization header");
    }
}
