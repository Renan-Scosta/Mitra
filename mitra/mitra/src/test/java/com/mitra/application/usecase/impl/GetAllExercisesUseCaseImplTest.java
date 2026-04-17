package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.enums.TrackingType;
import com.mitra.presentation.dto.response.ExerciseResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllExercisesUseCaseImplTest {

    @Mock
    private ExerciseRepositoryPort exerciseRepositoryPort;

    private GetAllExercisesUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetAllExercisesUseCaseImpl(exerciseRepositoryPort);
    }

    @Test
    void shouldReturnAllExercisesMappedToDto() {
        List<Exercise> exercises = List.of(
                Exercise.builder().id(1L).name("Squat").muscleGroup("Legs")
                        .metFactor(new BigDecimal("8.0")).trackingType(TrackingType.WEIGHT_REPS).build(),
                Exercise.builder().id(2L).name("Plank").muscleGroup("Core")
                        .metFactor(new BigDecimal("3.0")).trackingType(TrackingType.TIME_ONLY).build()
        );

        when(exerciseRepositoryPort.findAll()).thenReturn(exercises);

        List<ExerciseResponseDto> result = useCase.execute();

        assertEquals(2, result.size());
        assertEquals("Squat", result.get(0).name());
        assertEquals(TrackingType.WEIGHT_REPS, result.get(0).trackingType());
        assertEquals("Plank", result.get(1).name());
        assertEquals(TrackingType.TIME_ONLY, result.get(1).trackingType());
    }

    @Test
    void shouldReturnEmptyListWhenNoExercises() {
        when(exerciseRepositoryPort.findAll()).thenReturn(List.of());

        List<ExerciseResponseDto> result = useCase.execute();

        assertTrue(result.isEmpty());
    }
}
