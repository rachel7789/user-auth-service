package com.example.rachelklein.userauth.exception;

public class AccountInactiveException extends RuntimeException {
    public AccountInactiveException() {
        super("Account is inactive");
    }
}
