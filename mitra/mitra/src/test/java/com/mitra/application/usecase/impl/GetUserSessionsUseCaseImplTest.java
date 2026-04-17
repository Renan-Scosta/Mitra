package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.WorkoutSessionResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserSessionsUseCaseImplTest {

    @Mock
    private WorkoutSessionRepositoryPort workoutSessionRepositoryPort;

    private GetUserSessionsUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetUserSessionsUseCaseImpl(workoutSessionRepositoryPort);
    }

    @Test
    void shouldReturnAllSessionsForUser() {
        List<WorkoutSession> sessions = List.of(
                WorkoutSession.builder().id(1L).userId(1L).routineId(10L)
                        .startTime(LocalDateTime.now().minusDays(1))
                        .endTime(LocalDateTime.now().minusDays(1).plusHours(1)).build(),
                WorkoutSession.builder().id(2L).userId(1L).routineId(10L)
                        .startTime(LocalDateTime.now()).build()
        );

        when(workoutSessionRepositoryPort.findByUserId(1L)).thenReturn(sessions);

        List<WorkoutSessionResponseDto> result = useCase.execute(1L);

        assertEquals(2, result.size());
        assertFalse(result.get(0).active());
        assertTrue(result.get(1).active());
    }

    @Test
    void shouldReturnEmptyListWhenNoSessions() {
        when(workoutSessionRepositoryPort.findByUserId(99L)).thenReturn(List.of());

        List<WorkoutSessionResponseDto> result = useCase.execute(99L);

        assertTrue(result.isEmpty());
    }
}
