package com.mitra.presentation.controller;

import com.mitra.application.usecase.CalculateBmrUseCase;
import com.mitra.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalculateBmrUseCase calculateBmrUseCase;

    @MockitoBean
    private com.mitra.application.usecase.RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private com.mitra.application.port.out.UserRepositoryPort userRepositoryPort;

    @MockitoBean
    private com.mitra.infrastructure.security.TokenService tokenService;

    @BeforeEach
    void setUp() {
        User testUser = User.builder().id(1L).email("test@mitra.com").name("Test").password("x").build();
        var auth = new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void shouldReturnBmrWhenUserExists() throws Exception {
        when(calculateBmrUseCase.execute(1L)).thenReturn(1850.5);

        mockMvc.perform(get("/api/v1/users/me/bmr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bmr").value(1850.5))
                .andExpect(jsonPath("$.calculatedAt").exists());
    }

    @Test
    void shouldReturn404WhenUserOrMeasurementNotFound() throws Exception {
        when(calculateBmrUseCase.execute(1L))
                .thenThrow(new IllegalArgumentException("User not found: 1"));

        mockMvc.perform(get("/api/v1/users/me/bmr"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateUserAndReturn201() throws Exception {
        when(registerUserUseCase.execute(any())).thenReturn(1L);

        String payload = """
                {
                    "name": "Test User",
                    "email": "test@mitra.com",
                    "birthDate": "2000-01-01",
                    "gender": "MALE",
                    "heightCm": 180,
                    "initialWeightKg": 80.5,
                    "password": "pass123",
                    "confirmPassword": "pass123"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }
}
