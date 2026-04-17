package com.mitra.presentation.controller;

import com.mitra.application.usecase.CreateBodyMeasurementUseCase;
import com.mitra.application.usecase.GetBodyMeasurementsUseCase;
import com.mitra.domain.model.User;
import com.mitra.presentation.dto.response.BodyMeasurementResponseDto;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BodyMeasurementController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class BodyMeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateBodyMeasurementUseCase createBodyMeasurementUseCase;

    @MockitoBean
    private GetBodyMeasurementsUseCase getBodyMeasurementsUseCase;

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
    void shouldCreateMeasurementAndReturn201() throws Exception {
        when(createBodyMeasurementUseCase.execute(eq(1L), any())).thenReturn(10L);

        String payload = """
                {
                    "weightKg": 82.5,
                    "bodyFatPercentage": 19.0,
                    "recordDate": "2026-04-16"
                }
                """;

        mockMvc.perform(post("/api/v1/measurements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/measurements/10"));
    }

    @Test
    void shouldReturnMeasurementHistory() throws Exception {
        List<BodyMeasurementResponseDto> measurements = List.of(
                new BodyMeasurementResponseDto(1L, new BigDecimal("82.5"), new BigDecimal("19.0"),
                        new BigDecimal("66.83"), new BigDecimal("15.68"), LocalDate.of(2026, 4, 16))
        );

        when(getBodyMeasurementsUseCase.execute(1L)).thenReturn(measurements);

        mockMvc.perform(get("/api/v1/measurements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].weightKg").value(82.5))
                .andExpect(jsonPath("$[0].leanMassKg").value(66.83));
    }

    @Test
    void shouldReturn400ForInvalidMeasurement() throws Exception {
        String payload = """
                {
                    "recordDate": "2026-04-16"
                }
                """;

        mockMvc.perform(post("/api/v1/measurements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}
