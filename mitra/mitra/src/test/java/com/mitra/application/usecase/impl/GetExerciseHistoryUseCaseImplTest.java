package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.application.port.out.SetRecordRepositoryPort;
import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.SetRecord;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.ExerciseHistoryResponseDto;
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
class GetExerciseHistoryUseCaseImplTest {

    @Mock
    private ExerciseRepositoryPort exerciseRepositoryPort;

    @Mock
    private SetRecordRepositoryPort setRecordRepositoryPort;

    @Mock
    private WorkoutSessionRepositoryPort sessionRepositoryPort;

    @InjectMocks
    private GetExerciseHistoryUseCaseImpl useCase;

    @Test
    void shouldReturnHistoryGroupedBySessionAndSortedDesc() {
        Exercise bench = Exercise.builder().id(5L).name("Bench Press").build();
        
        SetRecord s1 = SetRecord.builder().id(1L).sessionId(100L).weightKg(new BigDecimal("100")).reps(10).build();
        SetRecord s2 = SetRecord.builder().id(2L).sessionId(100L).weightKg(new BigDecimal("100")).reps(8).build();
        SetRecord s3 = SetRecord.builder().id(3L).sessionId(200L).weightKg(new BigDecimal("105")).reps(5).build();

        WorkoutSession session1 = WorkoutSession.builder().id(100L).startTime(LocalDateTime.of(2026, 4, 1, 10, 0)).build();
        WorkoutSession session2 = WorkoutSession.builder().id(200L).startTime(LocalDateTime.of(2026, 4, 5, 10, 0)).build(); // NEWER

        when(exerciseRepositoryPort.findById(5L)).thenReturn(Optional.of(bench));
        when(setRecordRepositoryPort.findByUserIdAndExerciseId(1L, 5L)).thenReturn(List.of(s1, s2, s3));
        when(sessionRepositoryPort.findById(100L)).thenReturn(Optional.of(session1));
        when(sessionRepositoryPort.findById(200L)).thenReturn(Optional.of(session2));

        ExerciseHistoryResponseDto response = useCase.execute(1L, 5L);

        assertNotNull(response);
        assertEquals(5L, response.exerciseId());
        assertEquals("Bench Press", response.exerciseName());
        assertEquals(2, response.sessions().size());

        // Should be ordered descending, so sessionId 200 is first
        ExerciseHistoryResponseDto.SessionHistoryDto first = response.sessions().get(0);
        assertEquals(200L, first.sessionId());
        assertEquals(1, first.sets().size());
        assertEquals(5, first.sets().get(0).reps());

        ExerciseHistoryResponseDto.SessionHistoryDto second = response.sessions().get(1);
        assertEquals(100L, second.sessionId());
        assertEquals(2, second.sets().size());
    }

    @Test
    void shouldReturnEmptyHistoryForNoSets() {
        Exercise bench = Exercise.builder().id(5L).name("Bench Press").build();
        when(exerciseRepositoryPort.findById(5L)).thenReturn(Optional.of(bench));
        when(setRecordRepositoryPort.findByUserIdAndExerciseId(1L, 5L)).thenReturn(List.of());

        ExerciseHistoryResponseDto response = useCase.execute(1L, 5L);
        
        assertNotNull(response);
        assertTrue(response.sessions().isEmpty());
    }

    @Test
    void shouldThrowWhenExerciseNotFound() {
        when(exerciseRepositoryPort.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L, 99L));
    }
}
