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
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_verify_login_flow_shouldSucceed() throws Exception {
        String email = "flow.user@example.com";
        String password = "SecurePass123";

        // 1) REGISTER
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
                """.formatted(email, password);

        String registerResponse = mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationToken", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.UID", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // חילוץ verificationToken מהתגובה (כדי להשתמש בו ב-verify)
        String verificationToken = objectMapper.readTree(registerResponse).get("verificationToken").asText();

        // 2) VERIFY
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

        // 3) LOGIN
        String loginJson = """
                {
                  "loginID": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);

        mockMvc.perform(post("/api/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionToken", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.UID", notNullValue()))
                .andExpect(jsonPath("$.profile.email", is(email)))
                .andExpect(jsonPath("$.profile.verified", is(true)));
    }
}
