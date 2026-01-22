package com.example.rachelklein.userauth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class LoginResponse {

    @JsonProperty("UID")
    private UUID uid;
    private String sessionToken;
    private Profile profile;
    private int statusCode;
    private String refreshToken;

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static class Profile {
        private String firstName;
        private String lastName;
        private String email;
        @JsonProperty("isVerified")
        private boolean isVerified;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public boolean isVerified() {
            return isVerified;
        }

        public void setVerified(boolean verified) {
            isVerified = verified;
        }
    }
}
