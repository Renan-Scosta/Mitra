package com.mitra.presentation.controller;

import com.mitra.application.usecase.LogSetRecordUseCase;
import com.mitra.application.usecase.StartWorkoutSessionUseCase;
import com.mitra.application.usecase.FinishWorkoutSessionUseCase;
import com.mitra.application.usecase.GetWorkoutSessionUseCase;
import com.mitra.presentation.dto.response.SetRecordResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@WebMvcTest(WorkoutSessionController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class WorkoutSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StartWorkoutSessionUseCase startWorkoutSessionUseCase;

    @MockitoBean
    private LogSetRecordUseCase logSetRecordUseCase;

    @MockitoBean
    private FinishWorkoutSessionUseCase finishWorkoutSessionUseCase;

    @MockitoBean
    private GetWorkoutSessionUseCase getWorkoutSessionUseCase;

    @MockitoBean
    private com.mitra.application.port.out.UserRepositoryPort userRepositoryPort;

    @MockitoBean
    private com.mitra.infrastructure.security.TokenService tokenService;

    @Test
    void shouldStartSessionAndReturn201() throws Exception {
        when(startWorkoutSessionUseCase.execute(any())).thenReturn(100L);

        String payload = """
                {
                    "userId": 1,
                    "routineId": 10
                }
                """;

        mockMvc.perform(post("/api/v1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/api/v1/sessions/100"));
    }

    @Test
    void shouldLogSetAndReturn200() throws Exception {
        SetRecordResponseDto responseDto = new SetRecordResponseDto(30L, 5L, new BigDecimal("45.0"), 12, null);
        when(logSetRecordUseCase.execute(eq(100L), any())).thenReturn(responseDto);

        String payload = """
                {
                    "exerciseId": 5,
                    "weightKg": 45.0,
                    "reps": 12
                }
                """;

        mockMvc.perform(post("/api/v1/sessions/100/sets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(30))
                .andExpect(jsonPath("$.reps").value(12));
    }

    @Test
    void shouldFinishSessionAndReturn200() throws Exception {
        com.mitra.presentation.dto.response.SessionSummaryResponseDto summary = 
            new com.mitra.presentation.dto.response.SessionSummaryResponseDto(100L, 15, 60L);
            
        when(finishWorkoutSessionUseCase.execute(100L)).thenReturn(summary);

        mockMvc.perform(post("/api/v1/sessions/100/finish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSets").value(15))
                .andExpect(jsonPath("$.effectiveDurationMinutes").value(60));
    }

    @Test
    void shouldGetSessionDetailsAndReturn200() throws Exception {
        com.mitra.presentation.dto.response.WorkoutSessionResponseDto sessionDto = 
            new com.mitra.presentation.dto.response.WorkoutSessionResponseDto(
                100L, 1L, 10L, java.time.LocalDateTime.now(), null, true, java.util.List.of()
            );

        when(getWorkoutSessionUseCase.execute(100L)).thenReturn(sessionDto);

        mockMvc.perform(get("/api/v1/sessions/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.active").value(true));
    }
}
