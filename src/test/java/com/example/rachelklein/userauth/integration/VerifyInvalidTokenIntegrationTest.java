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
class VerifyInvalidTokenIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void verify_withInvalidTokenForExistingUser_shouldReturn400_invalidToken() throws Exception {
        String email = "invalid.verify@example.com";
        String password = "SecurePass123";

        // REGISTER so that the user exists.
        String registerJson = """
                {
                  "email": "%s",
                  "password": "%s",
                  "profile": {
                    "firstName": "John",
                    "lastName": "Doe"
                  }
                }
                """.formatted(email, password);

        mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationToken", not(isEmptyOrNullString())));

        // VERIFY with an invalid token.
        String verifyJson = """
                {
                  "token": "%s",
                  "email": "%s"
                }
                """.formatted("WRONG_TOKEN", email);

        mockMvc.perform(post("/api/accounts/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(verifyJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400)))
                .andExpect(jsonPath("$.errorCode", is("INVALID_TOKEN")))
                .andExpect(jsonPath("$.errorMessage", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
