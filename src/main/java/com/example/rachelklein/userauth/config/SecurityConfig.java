package com.example.rachelklein.userauth.config;

import com.example.rachelklein.userauth.dto.error.ErrorResponse;
import com.example.rachelklein.userauth.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .headers(h -> h.frameOptions(frame -> frame.disable()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                            ErrorResponse er = new ErrorResponse();
                            er.setStatusCode(401);
                            er.setErrorCode("UNAUTHORIZED");
                            er.setErrorMessage("Missing or invalid Authorization header");
                            er.setTimestamp(LocalDateTime.now());

                            new ObjectMapper().writeValue(response.getOutputStream(), er);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                            ErrorResponse er = new ErrorResponse();
                            er.setStatusCode(403);
                            er.setErrorCode("FORBIDDEN");
                            er.setErrorMessage("Access denied");
                            er.setTimestamp(LocalDateTime.now());

                            new ObjectMapper().writeValue(response.getOutputStream(), er);
                        })
                ).authorizeHttpRequests(auth -> auth
                        // Swagger + OpenAPI
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // H2 console (development)
                        .requestMatchers("/h2-console/**").permitAll()

                        // Public endpoints
                        .requestMatchers(
                                "/api/accounts/register",
                                "/api/accounts/login",
                                "/api/accounts/verify",
                                "/api/accounts/password/reset-request",
                                "/api/accounts/password/reset",
                                "/api/accounts/token/refresh"
                        ).permitAll()

                        // Protected endpoints
                        .requestMatchers(
                                "/api/accounts/info",
                                "/api/accounts/profile"
                        ).authenticated()

                        .anyRequest().denyAll()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
