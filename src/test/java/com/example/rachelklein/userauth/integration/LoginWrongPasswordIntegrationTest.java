package com.example.rachelklein.userauth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginWrongPasswordIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_withWrongPassword_shouldReturn401_invalidCredentials() throws Exception {
        String email = "wrong.pass.user@example.com";
        String correctPassword = "SecurePass123";
        String wrongPassword = "SecurePass999";

        // REGISTER
        String registerJson = """
                {
                  "email": "%s",
                  "password": "%s",
                  "profile": {
                    "firstName": "John",
                    "lastName": "Doe",
                    "birthDate": "1990-01-15",
                    "phoneNumber": "+972501234567"
                  }
                }
                """.formatted(email, correctPassword);

        String registerResponse = mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationToken", not(isEmptyOrNullString())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String verificationToken = objectMapper.readTree(registerResponse).get("verificationToken").asText();

        // VERIFY
        String verifyJson = """
                {
                  "token": "%s",
                  "email": "%s"
                }
                """.formatted(verificationToken, email);

        mockMvc.perform(post("/api/accounts/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(verifyJson))
                .andExpect(status().isOk());

        // LOGIN with an incorrect password.
        String loginJson = """
                {
                  "loginID": "%s",
                  "password": "%s"
                }
                """.formatted(email, wrongPassword);

        mockMvc.perform(post("/api/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode", is(401)))
                .andExpect(jsonPath("$.errorCode", is("INVALID_CREDENTIALS")))
                .andExpect(jsonPath("$.errorMessage", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
