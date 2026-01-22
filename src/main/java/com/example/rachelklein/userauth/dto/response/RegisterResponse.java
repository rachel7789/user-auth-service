package com.example.rachelklein.userauth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class RegisterResponse {

    @JsonProperty("UID")
    private UUID uid;
    private int statusCode;
    private String statusMessage;
    @JsonProperty("emailVerificationRequired")
    private boolean emailVerificationRequired;
    private String verificationToken;

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public boolean isEmailVerificationRequired() {
        return emailVerificationRequired;
    }

    public void setEmailVerificationRequired(boolean emailVerificationRequired) {
        this.emailVerificationRequired = emailVerificationRequired;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }
}
