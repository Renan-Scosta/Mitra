package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.WorkoutSessionResponseDto;
import com.mitra.presentation.dto.response.SetRecordResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
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

        Page<WorkoutSession> pageRecord = new PageImpl<>(sessions);
        when(workoutSessionRepositoryPort.findByUserIdAndDateRange(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(pageRecord);

        Page<WorkoutSessionResponseDto> result = useCase.execute(1L, null, null, PageRequest.of(0, 10));

        assertEquals(2, result.getContent().size());
        assertFalse(result.getContent().get(0).active());
        assertTrue(result.getContent().get(1).active());
    }

    @Test
    void shouldReturnEmptyListWhenNoSessions() {
        when(workoutSessionRepositoryPort.findByUserIdAndDateRange(eq(99L), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        Page<WorkoutSessionResponseDto> result = useCase.execute(99L, LocalDate.now(), LocalDate.now(), PageRequest.of(0, 10));

        assertTrue(result.isEmpty());
    }
}
