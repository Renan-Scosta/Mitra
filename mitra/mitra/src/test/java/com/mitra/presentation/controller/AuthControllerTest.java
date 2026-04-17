package com.mitra.presentation.controller;

import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.domain.model.User;
import com.mitra.infrastructure.security.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepositoryPort userRepositoryPort;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldReturnTokenForValidCredentials() throws Exception {
        User user = User.builder()
                .id(1L).email("dev@mitra.com").name("Dev").password("$2a$encoded").build();

        when(userRepositoryPort.findByEmail("dev@mitra.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "$2a$encoded")).thenReturn(true);
        when(tokenService.generateToken(eq("dev@mitra.com"), eq(1L))).thenReturn("jwt-token-123");

        String payload = """
                {
                    "email": "dev@mitra.com",
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"));
    }

    @Test
    void shouldReturn401ForInvalidPassword() throws Exception {
        User user = User.builder()
                .id(1L).email("dev@mitra.com").name("Dev").password("$2a$encoded").build();

        when(userRepositoryPort.findByEmail("dev@mitra.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "$2a$encoded")).thenReturn(false);

        String payload = """
                {
                    "email": "dev@mitra.com",
                    "password": "wrong-password"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401ForNonexistentEmail() throws Exception {
        when(userRepositoryPort.findByEmail("ghost@mitra.com")).thenReturn(Optional.empty());

        String payload = """
                {
                    "email": "ghost@mitra.com",
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400ForMissingFields() throws Exception {
        String payload = """
                {
                    "email": ""
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}
