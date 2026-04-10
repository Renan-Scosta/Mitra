package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.SetRecord;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.WorkoutSessionResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetWorkoutSessionUseCaseImplTest {

    @Mock
    private WorkoutSessionRepositoryPort workoutSessionRepositoryPort;

    @InjectMocks
    private GetWorkoutSessionUseCaseImpl getWorkoutSessionUseCase;

    @Test
    void shouldGetSessionDetails() {
        Exercise exercise = Exercise.builder().id(5L).build();
        SetRecord record = SetRecord.builder().id(30L).reps(10).weightKg(new BigDecimal("40.0")).exercise(exercise).build();
        
        WorkoutSession session = WorkoutSession.builder()
                .id(100L)
                .userId(1L)
                .startTime(LocalDateTime.now())
                .setRecords(List.of(record))
                .build();
                
        when(workoutSessionRepositoryPort.findById(100L)).thenReturn(Optional.of(session));

        WorkoutSessionResponseDto response = getWorkoutSessionUseCase.execute(100L);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertTrue(response.active());
        assertEquals(1, response.setRecords().size());
        assertEquals(30L, response.setRecords().get(0).id());
    }

    @Test
    void shouldThrowExceptionWhenSessionNotFound() {
        when(workoutSessionRepositoryPort.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> getWorkoutSessionUseCase.execute(99L));
    }
}
