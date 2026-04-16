package com.mitra.presentation.controller;

import com.mitra.application.usecase.AddRoutineExerciseUseCase;
import com.mitra.application.usecase.CreateWorkoutRoutineUseCase;
import com.mitra.application.usecase.GetWorkoutRoutinesUseCase;
import com.mitra.domain.model.User;
import com.mitra.presentation.dto.response.ExerciseResponseDto;
import com.mitra.presentation.dto.response.RoutineExerciseResponseDto;
import com.mitra.presentation.dto.response.RoutineResponseDto;
import com.mitra.domain.model.enums.TrackingType;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(RoutineController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class RoutineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateWorkoutRoutineUseCase createWorkoutRoutineUseCase;

    @MockitoBean
    private GetWorkoutRoutinesUseCase getWorkoutRoutinesUseCase;

    @MockitoBean
    private AddRoutineExerciseUseCase addRoutineExerciseUseCase;

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
    void shouldCreateRoutineAndReturn201() throws Exception {
        when(createWorkoutRoutineUseCase.execute(any(), any())).thenReturn(10L);

        String payload = """
                {
                    "name": "Full Body A"
                }
                """;

        mockMvc.perform(post("/api/v1/routines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void shouldReturnUserRoutines() throws Exception {
        ExerciseResponseDto exDto = new ExerciseResponseDto(5L, "Squat", "Legs", new BigDecimal("7.0"), TrackingType.WEIGHT_REPS);
        RoutineExerciseResponseDto responseDto = new RoutineExerciseResponseDto(25L, exDto, 4, 10);
        
        when(getWorkoutRoutinesUseCase.execute(1L)).thenReturn(List.of(
                new RoutineResponseDto(10L, 1L, "Full Body A", List.of(responseDto))
        ));

        mockMvc.perform(get("/api/v1/routines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Full Body A"))
                .andExpect(jsonPath("$[0].exercises").isArray())
                .andExpect(jsonPath("$[0].exercises[0].id").value(25));
    }

    @Test
    void shouldAddExerciseToRoutine() throws Exception {
        ExerciseResponseDto exDto = new ExerciseResponseDto(5L, "Squat", "Legs", new BigDecimal("7.0"), TrackingType.WEIGHT_REPS);
        RoutineExerciseResponseDto responseDto = new RoutineExerciseResponseDto(25L, exDto, 4, 10);

        when(addRoutineExerciseUseCase.execute(eq(10L), any())).thenReturn(responseDto);

        String payload = """
                {
                    "exerciseId": 5,
                    "targetSets": 4,
                    "targetReps": 10
                }
                """;

        mockMvc.perform(post("/api/v1/routines/10/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(25))
                .andExpect(jsonPath("$.targetSets").value(4));
    }
}
