package com.example.rachelklein.userauth.dto.response;

public class RefreshTokenResponse {

    private String sessionToken;
    private int statusCode;

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}

