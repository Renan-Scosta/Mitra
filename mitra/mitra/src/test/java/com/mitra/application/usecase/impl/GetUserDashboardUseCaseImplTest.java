package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.application.usecase.CalculateSessionCaloriesUseCase;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.DashboardResponseDto;
import com.mitra.presentation.dto.response.SessionCaloriesResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserDashboardUseCaseImplTest {

    @Mock
    private WorkoutSessionRepositoryPort sessionRepositoryPort;

    @Mock
    private CalculateSessionCaloriesUseCase calculateSessionCaloriesUseCase;

    private GetUserDashboardUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetUserDashboardUseCaseImpl(sessionRepositoryPort, calculateSessionCaloriesUseCase);
    }

    @Test
    void shouldReturnDashboardWithStreakAndCalories() {
        LocalDateTime now = LocalDateTime.now();
        WorkoutSession session1 = WorkoutSession.builder()
                .id(1L).userId(1L).startTime(now.minusDays(1)).endTime(now.minusDays(1).plusHours(1)).build();
        WorkoutSession session2 = WorkoutSession.builder()
                .id(2L).userId(1L).startTime(now).endTime(now.plusHours(1)).build();

        when(sessionRepositoryPort.findByUserId(1L)).thenReturn(List.of(session1, session2));
        when(calculateSessionCaloriesUseCase.execute(1L, 1L))
                .thenReturn(new SessionCaloriesResponseDto(1L, 300.0, List.of()));
        when(calculateSessionCaloriesUseCase.execute(1L, 2L))
                .thenReturn(new SessionCaloriesResponseDto(2L, 400.0, List.of()));

        DashboardResponseDto dashboard = useCase.execute(1L);

        assertNotNull(dashboard);
        assertEquals(2, dashboard.workoutsThisWeek());
        assertEquals(2, dashboard.currentStreak());
        assertEquals(700.0, dashboard.totalCaloriesThisWeek());
        assertNotNull(dashboard.lastWorkout());
        assertEquals(2L, dashboard.lastWorkout().id());
    }

    @Test
    void shouldReturnEmptyDashboardWhenNoSessions() {
        when(sessionRepositoryPort.findByUserId(1L)).thenReturn(List.of());

        DashboardResponseDto dashboard = useCase.execute(1L);

        assertEquals(0, dashboard.workoutsThisWeek());
        assertEquals(0, dashboard.currentStreak());
        assertEquals(0.0, dashboard.totalCaloriesThisWeek());
    }
}
