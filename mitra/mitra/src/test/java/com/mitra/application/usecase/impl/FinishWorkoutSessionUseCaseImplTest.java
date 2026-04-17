package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.domain.model.BodyMeasurement;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.SetRecord;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.SessionSummaryResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mitra.domain.model.enums.TrackingType;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinishWorkoutSessionUseCaseImplTest {

    @Mock
    private WorkoutSessionRepositoryPort workoutSessionRepositoryPort;

    @Mock
    private BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort;

    @InjectMocks
    private FinishWorkoutSessionUseCaseImpl finishWorkoutSessionUseCase;

    @Test
    void shouldFinishSessionSuccessfullyAndCalculateCalories() {
        Long userId = 1L;
        Exercise squat = Exercise.builder().name("Squat").metFactor(new BigDecimal("7.0")).trackingType(TrackingType.WEIGHT_REPS).build();
        SetRecord record = SetRecord.builder().exercise(squat).reps(10).build(); // 25s -> 7 * 80 * 25/3600 = ~3.9

        WorkoutSession session = WorkoutSession.builder()
                .id(100L).userId(userId)
                .startTime(LocalDateTime.now().minusMinutes(45))
                .setRecords(List.of(record))
                .build();
        
        BodyMeasurement measurement = BodyMeasurement.builder().weightKg(new BigDecimal("80.0")).build();

        when(workoutSessionRepositoryPort.findById(100L)).thenReturn(Optional.of(session));
        when(bodyMeasurementRepositoryPort.findLatestByUserId(userId)).thenReturn(Optional.of(measurement));
        when(workoutSessionRepositoryPort.save(any(WorkoutSession.class))).thenReturn(session);

        SessionSummaryResponseDto summary = finishWorkoutSessionUseCase.execute(userId, 100L);

        assertNotNull(summary);
        assertEquals(100L, summary.sessionId());
        assertEquals(1, summary.totalSets());
        assertEquals(45L, summary.effectiveDurationMinutes());
        assertEquals(3.9, summary.estimatedCalories());
        assertFalse(session.isActive());
        verify(workoutSessionRepositoryPort, times(1)).save(session);
    }

    @Test
    void shouldFinishSessionWithoutCaloriesWhenNoMeasurement() {
        Long userId = 1L;
        WorkoutSession session = WorkoutSession.builder()
                .id(100L).userId(userId)
                .startTime(LocalDateTime.now().minusMinutes(45))
                .setRecords(List.of(new SetRecord()))
                .build();

        when(workoutSessionRepositoryPort.findById(100L)).thenReturn(Optional.of(session));
        when(bodyMeasurementRepositoryPort.findLatestByUserId(userId)).thenReturn(Optional.empty());
        when(workoutSessionRepositoryPort.save(any(WorkoutSession.class))).thenReturn(session);

        SessionSummaryResponseDto summary = finishWorkoutSessionUseCase.execute(userId, 100L);

        assertNotNull(summary);
        assertNull(summary.estimatedCalories());
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
