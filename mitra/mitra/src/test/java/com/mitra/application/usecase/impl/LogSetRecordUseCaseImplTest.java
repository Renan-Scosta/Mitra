package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.application.port.out.SetRecordRepositoryPort;
import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.SetRecord;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.domain.model.enums.TrackingType;
import com.mitra.presentation.dto.request.LogSetRequestDto;
import com.mitra.presentation.dto.response.SetRecordResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogSetRecordUseCaseImplTest {

    @Mock
    private WorkoutSessionRepositoryPort workoutSessionRepositoryPort;

    @Mock
    private ExerciseRepositoryPort exerciseRepositoryPort;

    @Mock
    private SetRecordRepositoryPort setRecordRepositoryPort;

    @InjectMocks
    private LogSetRecordUseCaseImpl logSetRecordUseCase;

    @Test
    void shouldLogSetSuccessfully() {
        Long sessionId = 100L;
        LogSetRequestDto request = new LogSetRequestDto(5L, new BigDecimal("40.0"), 10, null);
        
        WorkoutSession session = WorkoutSession.builder().id(sessionId).startTime(LocalDateTime.now()).build();
        Exercise exercise = Exercise.builder().id(5L).trackingType(TrackingType.WEIGHT_REPS).build();
        
        when(workoutSessionRepositoryPort.findById(sessionId)).thenReturn(Optional.of(session));
        when(exerciseRepositoryPort.findById(5L)).thenReturn(Optional.of(exercise));
        when(setRecordRepositoryPort.save(any(SetRecord.class))).thenAnswer(i -> {
            SetRecord s = i.getArgument(0);
            return SetRecord.builder()
                    .id(30L)
                    .sessionId(s.getSessionId())
                    .exercise(s.getExercise())
                    .weightKg(s.getWeightKg())
                    .reps(s.getReps())
                    .build();
        });

        SetRecordResponseDto response = logSetRecordUseCase.execute(sessionId, request);

        assertNotNull(response);
        assertEquals(30L, response.id());
        assertEquals(5L, response.exerciseId());
        assertEquals(40.0, response.weightKg().doubleValue());
        assertEquals(10, response.reps());
        verify(setRecordRepositoryPort, times(1)).save(any(SetRecord.class));
    }

    @Test
    void shouldThrowExceptionWhenSessionNotFound() {
        LogSetRequestDto request = new LogSetRequestDto(5L, new BigDecimal("40.0"), 10, null);
        when(workoutSessionRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> logSetRecordUseCase.execute(99L, request));
    }

    @Test
    void shouldThrowExceptionWhenSessionIsNotActive() {
        LogSetRequestDto request = new LogSetRequestDto(5L, new BigDecimal("40.0"), 10, null);
        WorkoutSession session = WorkoutSession.builder()
                .id(100L)
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now()) // session is finished
                .build();
                
        when(workoutSessionRepositoryPort.findById(100L)).thenReturn(Optional.of(session));

        assertThrows(IllegalStateException.class, () -> logSetRecordUseCase.execute(100L, request));
    }
}
