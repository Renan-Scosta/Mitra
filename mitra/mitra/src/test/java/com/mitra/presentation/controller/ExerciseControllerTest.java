package com.mitra.presentation.controller;

import com.mitra.application.usecase.CreateExerciseUseCase;
import com.mitra.application.usecase.GetAllExercisesUseCase;
import com.mitra.application.usecase.GetExerciseHistoryUseCase;
import com.mitra.application.usecase.GetPersonalRecordsUseCase;
import com.mitra.presentation.dto.response.ExerciseHistoryResponseDto;
import com.mitra.presentation.dto.response.ExerciseResponseDto;
import com.mitra.presentation.dto.response.PersonalRecordResponseDto;
import com.mitra.domain.model.enums.TrackingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.mitra.domain.model.User;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@WebMvcTest(ExerciseController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateExerciseUseCase createExerciseUseCase;

    @MockitoBean
    private GetAllExercisesUseCase getAllExercisesUseCase;

    @MockitoBean
    private GetExerciseHistoryUseCase getExerciseHistoryUseCase;

    @MockitoBean
    private GetPersonalRecordsUseCase getPersonalRecordsUseCase;

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
    void shouldCreateExerciseAndReturn201() throws Exception {
        when(createExerciseUseCase.execute(any())).thenReturn(1L);

        String payload = """
                {
                    "name": "Bench Press",
                    "muscleGroup": "Chest",
                    "metFactor": 6.0,
                    "trackingType": "WEIGHT_REPS"
                }
                """;

        mockMvc.perform(post("/api/v1/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void shouldReturnAllExercises() throws Exception {
        when(getAllExercisesUseCase.execute()).thenReturn(List.of(
                new ExerciseResponseDto(1L, "Bench", "Chest", new BigDecimal("6.0"), TrackingType.WEIGHT_REPS)
        ));

        mockMvc.perform(get("/api/v1/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bench"));
    }

    @Test
    void shouldReturnExerciseHistory() throws Exception {
        User testUser = User.builder().id(1L).email("test@").name("Test").password("x").build();
        var auth = new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList());

        ExerciseHistoryResponseDto.SetHistoryDto setDto = new ExerciseHistoryResponseDto.SetHistoryDto(new BigDecimal("100"), 10, null);
        ExerciseHistoryResponseDto.SessionHistoryDto sessionDto = new ExerciseHistoryResponseDto.SessionHistoryDto(100L, java.time.LocalDateTime.now(), List.of(setDto));
        ExerciseHistoryResponseDto responseDto = new ExerciseHistoryResponseDto(5L, "Bench", List.of(sessionDto));

        when(getExerciseHistoryUseCase.execute(any(), any())).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/exercises/5/history").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseName").value("Bench"))
                .andExpect(jsonPath("$.sessions[0].sessionId").value(100L));
    }

    @Test
    void shouldReturnPersonalRecords() throws Exception {
        User testUser = User.builder().id(1L).email("test@").name("Test").password("x").build();
        var auth = new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList());

        PersonalRecordResponseDto responseDto = new PersonalRecordResponseDto(5L, "Bench", new BigDecimal("120.0"), 12, new BigDecimal("1080"), null, 3);

        when(getPersonalRecordsUseCase.execute(any(), any())).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/exercises/5/records").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseName").value("Bench"))
                .andExpect(jsonPath("$.maxWeight").value(120.0))
                .andExpect(jsonPath("$.maxReps").value(12))
                .andExpect(jsonPath("$.maxVolume").value(1080))
                .andExpect(jsonPath("$.totalSets").value(3));
    }
}
