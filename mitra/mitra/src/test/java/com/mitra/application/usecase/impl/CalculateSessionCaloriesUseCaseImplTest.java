package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.domain.model.BodyMeasurement;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.SetRecord;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.domain.model.enums.TrackingType;
import com.mitra.presentation.dto.response.SessionCaloriesResponseDto;
import org.junit.jupiter.api.BeforeEach;
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
class CalculateSessionCaloriesUseCaseImplTest {

    @Mock
    private WorkoutSessionRepositoryPort workoutSessionRepositoryPort;

    @Mock
    private BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort;

    @InjectMocks
    private CalculateSessionCaloriesUseCaseImpl useCase;

    private WorkoutSession session;
    private BodyMeasurement measurement;

    @BeforeEach
    void setUp() {
        Exercise squat = Exercise.builder().name("Squat").metFactor(new BigDecimal("7.0")).trackingType(TrackingType.WEIGHT_REPS).build();
        SetRecord record = SetRecord.builder().exercise(squat).reps(10).build();
        
        session = WorkoutSession.builder()
                .id(100L).userId(1L).startTime(LocalDateTime.now())
                .setRecords(List.of(record)).build();
                
        measurement = BodyMeasurement.builder().weightKg(new BigDecimal("80.0")).build();
    }

    @Test
    void shouldCalculateCaloriesForSession() {
        when(workoutSessionRepositoryPort.findById(100L)).thenReturn(Optional.of(session));
        when(bodyMeasurementRepositoryPort.findLatestByUserId(1L)).thenReturn(Optional.of(measurement));

        SessionCaloriesResponseDto dto = useCase.execute(1L, 100L);

        assertNotNull(dto);
        assertEquals(100L, dto.sessionId());
        assertEquals(3.9, dto.totalCalories()); // 7 MET * 80kg * (25/3600) = 3.88 -> 3.9
        assertEquals(1, dto.perExercise().size());
        assertEquals("Squat", dto.perExercise().get(0).exerciseName());
        assertEquals(3.9, dto.perExercise().get(0).calories());
    }

    @Test
    void shouldThrowWhenSessionNotFound() {
        when(workoutSessionRepositoryPort.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L, 99L));
    }

    @Test
    void shouldThrowSecurityExceptionForOwnership() {
        when(workoutSessionRepositoryPort.findById(100L)).thenReturn(Optional.of(session));
        assertThrows(SecurityException.class, () -> useCase.execute(999L, 100L));
    }

    @Test
    void shouldThrowWhenNoBodyMeasurement() {
        when(workoutSessionRepositoryPort.findById(100L)).thenReturn(Optional.of(session));
        when(bodyMeasurementRepositoryPort.findLatestByUserId(1L)).thenReturn(Optional.empty());
        
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> useCase.execute(1L, 100L));
        assertTrue(ex.getMessage().contains("No body measurement found"));
    }
}
