package com.mitra.presentation.controller;

import com.mitra.application.usecase.CreateExerciseUseCase;
import com.mitra.application.usecase.GetAllExercisesUseCase;
import com.mitra.presentation.dto.response.ExerciseResponseDto;
import com.mitra.domain.model.enums.TrackingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(ExerciseController.class)
@ActiveProfiles("test")
class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateExerciseUseCase createExerciseUseCase;

    @MockitoBean
    private GetAllExercisesUseCase getAllExercisesUseCase;

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
}
