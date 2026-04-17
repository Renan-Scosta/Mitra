package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.application.port.out.RoutineExerciseRepositoryPort;
import com.mitra.application.port.out.WorkoutRoutineRepositoryPort;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.RoutineExercise;
import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.domain.model.enums.TrackingType;
import com.mitra.presentation.dto.request.AddRoutineExerciseRequestDto;
import com.mitra.presentation.dto.response.RoutineExerciseResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddRoutineExerciseUseCaseImplTest {

    @Mock
    private RoutineExerciseRepositoryPort routineExerciseRepositoryPort;

    @Mock
    private ExerciseRepositoryPort exerciseRepositoryPort;

    @Mock
    private WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort;

    private AddRoutineExerciseUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AddRoutineExerciseUseCaseImpl(
                routineExerciseRepositoryPort, exerciseRepositoryPort, workoutRoutineRepositoryPort);
    }

    @Test
    void shouldAddExerciseToRoutine() {
        Long userId = 1L;
        Long routineId = 10L;
        WorkoutRoutine routine = WorkoutRoutine.builder().id(routineId).userId(userId).build();

        Exercise exercise = Exercise.builder()
                .id(5L).name("Deadlift").muscleGroup("Back")
                .metFactor(new BigDecimal("6.0")).trackingType(TrackingType.WEIGHT_REPS)
                .build();

        AddRoutineExerciseRequestDto request = new AddRoutineExerciseRequestDto(5L, 3, 8);

        when(workoutRoutineRepositoryPort.findById(routineId)).thenReturn(Optional.of(routine));
        when(exerciseRepositoryPort.findById(5L)).thenReturn(Optional.of(exercise));
        when(routineExerciseRepositoryPort.save(any(RoutineExercise.class)))
                .thenReturn(RoutineExercise.builder()
                        .id(30L).routineId(routineId).exercise(exercise)
                        .targetSets(3).targetReps(8)
                        .build());

        RoutineExerciseResponseDto result = useCase.execute(userId, routineId, request);

        assertEquals(30L, result.id());
        assertEquals(3, result.targetSets());
        assertEquals(8, result.targetReps());
        assertEquals("Deadlift", result.exercise().name());
    }

    @Test
    void shouldThrowWhenRoutineNotFound() {
        AddRoutineExerciseRequestDto request = new AddRoutineExerciseRequestDto(5L, 3, 8);

        when(workoutRoutineRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L, 99L, request));
        verify(routineExerciseRepositoryPort, never()).save(any());
    }

    @Test
    void shouldThrowWhenExerciseNotFound() {
        Long userId = 1L;
        Long routineId = 10L;
        WorkoutRoutine routine = WorkoutRoutine.builder().id(routineId).userId(userId).build();
        AddRoutineExerciseRequestDto request = new AddRoutineExerciseRequestDto(999L, 3, 8);

        when(workoutRoutineRepositoryPort.findById(routineId)).thenReturn(Optional.of(routine));
        when(exerciseRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(userId, routineId, request));
        verify(routineExerciseRepositoryPort, never()).save(any());
    }

    @Test
    void shouldThrowSecurityExceptionWhenUserDoesNotOwnRoutine() {
        Long ownerId = 1L;
        Long attackerId = 999L;
        Long routineId = 10L;
        WorkoutRoutine routine = WorkoutRoutine.builder().id(routineId).userId(ownerId).build();
        AddRoutineExerciseRequestDto request = new AddRoutineExerciseRequestDto(5L, 3, 8);

        when(workoutRoutineRepositoryPort.findById(routineId)).thenReturn(Optional.of(routine));

        assertThrows(SecurityException.class, () -> useCase.execute(attackerId, routineId, request));
        verify(routineExerciseRepositoryPort, never()).save(any());
    }
}
