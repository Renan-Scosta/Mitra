package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.domain.model.SetRecord;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.SessionSummaryResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinishWorkoutSessionUseCaseImplTest {

    @Mock
    private WorkoutSessionRepositoryPort workoutSessionRepositoryPort;

    @InjectMocks
    private FinishWorkoutSessionUseCaseImpl finishWorkoutSessionUseCase;

    @Test
    void shouldFinishSessionSuccessfully() {
        Long userId = 1L;
        WorkoutSession session = WorkoutSession.builder()
                .id(100L).userId(userId)
                .startTime(LocalDateTime.now().minusMinutes(45))
                .setRecords(List.of(new SetRecord(), new SetRecord(), new SetRecord()))
                .build();

        when(workoutSessionRepositoryPort.findById(100L)).thenReturn(Optional.of(session));
        when(workoutSessionRepositoryPort.save(any(WorkoutSession.class))).thenReturn(session);

        SessionSummaryResponseDto summary = finishWorkoutSessionUseCase.execute(userId, 100L);

        assertNotNull(summary);
        assertEquals(100L, summary.sessionId());
        assertEquals(3, summary.totalSets());
        assertEquals(45L, summary.effectiveDurationMinutes());
        assertFalse(session.isActive());
        verify(workoutSessionRepositoryPort, times(1)).save(session);
    }

    @Test
    void shouldThrowExceptionWhenSessionNotFound() {
        when(workoutSessionRepositoryPort.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> finishWorkoutSessionUseCase.execute(1L, 99L));
    }

    @Test
    void shouldThrowSecurityExceptionWhenUserDoesNotOwnSession() {
        Long ownerId = 1L;
        Long attackerId = 999L;
        WorkoutSession session = WorkoutSession.builder()
                .id(100L).userId(ownerId)
                .startTime(LocalDateTime.now().minusMinutes(30))
                .build();

        when(workoutSessionRepositoryPort.findById(100L)).thenReturn(Optional.of(session));

        assertThrows(SecurityException.class,
                () -> finishWorkoutSessionUseCase.execute(attackerId, 100L));
        verify(workoutSessionRepositoryPort, never()).save(any());
    }
}
