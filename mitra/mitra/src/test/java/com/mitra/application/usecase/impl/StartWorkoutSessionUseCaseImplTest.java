package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutRoutineRepositoryPort;
import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.request.StartSessionRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartWorkoutSessionUseCaseImplTest {

    @Mock
    private WorkoutSessionRepositoryPort workoutSessionRepositoryPort;

    @Mock
    private WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort;

    @InjectMocks
    private StartWorkoutSessionUseCaseImpl startWorkoutSessionUseCase;

    @Test
    void shouldStartSessionSuccessfully() {
        StartSessionRequestDto request = new StartSessionRequestDto(1L, 10L);
        WorkoutRoutine routine = WorkoutRoutine.builder().id(10L).userId(1L).build();
        
        when(workoutRoutineRepositoryPort.findById(10L)).thenReturn(Optional.of(routine));
        when(workoutSessionRepositoryPort.save(any(WorkoutSession.class)))
                .thenAnswer(invocation -> {
                    WorkoutSession session = invocation.getArgument(0);
                    return WorkoutSession.builder()
                            .id(100L)
                            .routineId(session.getRoutineId())
                            .userId(session.getUserId())
                            .startTime(session.getStartTime())
                            .build();
                });

        Long sessionId = startWorkoutSessionUseCase.execute(request);

        assertNotNull(sessionId);
        assertEquals(100L, sessionId);
        verify(workoutSessionRepositoryPort, times(1)).save(any(WorkoutSession.class));
    }

    @Test
    void shouldThrowExceptionWhenRoutineNotFound() {
        StartSessionRequestDto request = new StartSessionRequestDto(1L, 99L);
        when(workoutRoutineRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> startWorkoutSessionUseCase.execute(request));
        verify(workoutSessionRepositoryPort, never()).save(any());
    }
}
