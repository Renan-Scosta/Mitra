package com.mitra.application.usecase.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mitra.application.port.out.WorkoutRoutineRepositoryPort;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.RoutineExercise;
import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.domain.model.enums.TrackingType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetWorkoutRoutinesUseCaseImplTest {

    @Mock private WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort;

    private GetWorkoutRoutinesUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetWorkoutRoutinesUseCaseImpl(workoutRoutineRepositoryPort);
    }

    @Test
    void shouldReturnRoutinesWithNestedExercises() {
        Exercise exercise =
                Exercise.builder()
                        .id(5L)
                        .name("Squat")
                        .muscleGroup("Legs")
                        .metFactor(new BigDecimal("8.0"))
                        .trackingType(TrackingType.WEIGHT_REPS)
                        .build();

        RoutineExercise re =
                RoutineExercise.builder()
                        .id(20L)
                        .routineId(10L)
                        .exercise(exercise)
                        .targetSets(4)
                        .targetReps(10)
                        .build();

        WorkoutRoutine routine =
                WorkoutRoutine.builder()
                        .id(10L)
                        .userId(1L)
                        .name("Full Body")
                        .routineExercises(List.of(re))
                        .build();

        when(workoutRoutineRepositoryPort.findByUserId(
                        eq(1L), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(routine)));

        var result = useCase.execute(1L, org.springframework.data.domain.PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Full Body", result.getContent().get(0).name());
        assertEquals(1, result.getContent().get(0).exercises().size());
        assertEquals("Squat", result.getContent().get(0).exercises().get(0).exercise().name());
        assertEquals(4, result.getContent().get(0).exercises().get(0).targetSets());
    }

    @Test
    void shouldReturnRoutineWithNullExerciseList() {
        WorkoutRoutine routine =
                WorkoutRoutine.builder()
                        .id(10L)
                        .userId(1L)
                        .name("Empty Routine")
                        .routineExercises(null)
                        .build();

        when(workoutRoutineRepositoryPort.findByUserId(
                        eq(1L), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(routine)));

        var result = useCase.execute(1L, org.springframework.data.domain.PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).exercises().isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoRoutines() {
        when(workoutRoutineRepositoryPort.findByUserId(
                        eq(99L), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());

        var result = useCase.execute(99L, org.springframework.data.domain.PageRequest.of(0, 10));

        assertTrue(result.isEmpty());
    }
}
