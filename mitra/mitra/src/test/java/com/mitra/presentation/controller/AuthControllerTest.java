package com.mitra.presentation.controller;

import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.domain.model.User;
import com.mitra.infrastructure.security.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) // Disable filters to test just the controller cleanly here
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepositoryPort userRepositoryPort;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Test
    void shouldReturnTokenWhenEmailExists() throws Exception {
        User user = User.builder().id(1L).email("test@mitra.com").build();
        when(userRepositoryPort.findByEmail("test@mitra.com")).thenReturn(Optional.of(user));
        when(tokenService.generateToken(eq("test@mitra.com"), eq(1L))).thenReturn("mock-jwt-token");
        when(passwordEncoder.matches(anyString(), any())).thenReturn(true);

        String payload = """
                {
                    "email": "test@mitra.com",
                    "password": "secret_password"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    void shouldReturn401WhenEmailDoesNotExist() throws Exception {
        when(userRepositoryPort.findByEmail(anyString())).thenReturn(Optional.empty());

        String payload = """
                {
                    "email": "unknown@mitra.com",
                    "password": "wrong_password"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }
}
