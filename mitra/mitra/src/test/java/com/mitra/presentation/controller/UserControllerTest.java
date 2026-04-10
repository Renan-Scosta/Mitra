package com.mitra.presentation.controller;

import com.mitra.application.usecase.CalculateBmrUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalculateBmrUseCase calculateBmrUseCase;

    @Test
    void shouldReturnBmrWhenUserExists() throws Exception {
        when(calculateBmrUseCase.execute(1L)).thenReturn(1850.5);

        mockMvc.perform(get("/api/v1/users/1/bmr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bmr").value(1850.5))
                .andExpect(jsonPath("$.calculatedAt").exists());
    }

    @Test
    void shouldReturn404WhenUserOrMeasurementNotFound() throws Exception {
        when(calculateBmrUseCase.execute(99L))
                .thenThrow(new IllegalArgumentException("User not found: 99"));

        mockMvc.perform(get("/api/v1/users/99/bmr"))
                .andExpect(status().isNotFound());
    }
}
