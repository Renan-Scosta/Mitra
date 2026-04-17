package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutRoutineRepositoryPort;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.RoutineExercise;
import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.domain.model.enums.TrackingType;
import com.mitra.presentation.dto.response.RoutineResponseDto;
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
class GetWorkoutRoutinesUseCaseImplTest {

    @Mock
    private WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort;

    private GetWorkoutRoutinesUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetWorkoutRoutinesUseCaseImpl(workoutRoutineRepositoryPort);
    }

    @Test
    void shouldReturnRoutinesWithNestedExercises() {
        Exercise exercise = Exercise.builder()
                .id(5L).name("Squat").muscleGroup("Legs")
                .metFactor(new BigDecimal("8.0")).trackingType(TrackingType.WEIGHT_REPS)
                .build();

        RoutineExercise re = RoutineExercise.builder()
                .id(20L).routineId(10L).exercise(exercise)
                .targetSets(4).targetReps(10)
                .build();

        WorkoutRoutine routine = WorkoutRoutine.builder()
                .id(10L).userId(1L).name("Full Body")
                .routineExercises(List.of(re))
                .build();

        when(workoutRoutineRepositoryPort.findByUserId(1L)).thenReturn(List.of(routine));

        List<RoutineResponseDto> result = useCase.execute(1L);

        assertEquals(1, result.size());
        assertEquals("Full Body", result.get(0).name());
        assertEquals(1, result.get(0).exercises().size());
        assertEquals("Squat", result.get(0).exercises().get(0).exercise().name());
        assertEquals(4, result.get(0).exercises().get(0).targetSets());
    }

    @Test
    void shouldReturnRoutineWithNullExerciseList() {
        WorkoutRoutine routine = WorkoutRoutine.builder()
                .id(10L).userId(1L).name("Empty Routine")
                .routineExercises(null)
                .build();

        when(workoutRoutineRepositoryPort.findByUserId(1L)).thenReturn(List.of(routine));

        List<RoutineResponseDto> result = useCase.execute(1L);

        assertEquals(1, result.size());
        assertTrue(result.get(0).exercises().isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoRoutines() {
        when(workoutRoutineRepositoryPort.findByUserId(99L)).thenReturn(List.of());

        List<RoutineResponseDto> result = useCase.execute(99L);

        assertTrue(result.isEmpty());
    }
}
