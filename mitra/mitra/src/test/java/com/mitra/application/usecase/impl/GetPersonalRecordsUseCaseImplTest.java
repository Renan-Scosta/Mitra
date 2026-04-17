package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.application.port.out.SetRecordRepositoryPort;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.SetRecord;
import com.mitra.application.exception.ResourceNotFoundException;
import com.mitra.presentation.dto.response.PersonalRecordResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPersonalRecordsUseCaseImplTest {

    @Mock
    private ExerciseRepositoryPort exerciseRepositoryPort;

    @Mock
    private SetRecordRepositoryPort setRecordRepositoryPort;

    @InjectMocks
    private GetPersonalRecordsUseCaseImpl useCase;

    @Test
    void shouldReturnPRsForWeightRepsExercise() {
        Exercise bench = Exercise.builder().id(5L).name("Bench Press").build();
        
        SetRecord s1 = SetRecord.builder().id(1L).weightKg(new BigDecimal("100")).reps(10).build();
        SetRecord s2 = SetRecord.builder().id(2L).weightKg(new BigDecimal("120")).reps(5).build();
        SetRecord s3 = SetRecord.builder().id(3L).weightKg(new BigDecimal("90")).reps(12).build();

        when(exerciseRepositoryPort.findById(5L)).thenReturn(Optional.of(bench));
        when(setRecordRepositoryPort.findByUserIdAndExerciseId(1L, 5L)).thenReturn(List.of(s1, s2, s3));

        PersonalRecordResponseDto response = useCase.execute(1L, 5L);

        assertNotNull(response);
        assertEquals(5L, response.exerciseId());
        assertEquals(new BigDecimal("120"), response.maxWeight());
        assertEquals(12, response.maxReps());
        assertEquals(new BigDecimal("1080"), response.maxVolume()); // 90 * 12
        assertNull(response.maxDuration());
        assertEquals(3, response.totalSets());
    }

    @Test
    void shouldReturnPRsForTimeOnlyExercise() {
        Exercise plank = Exercise.builder().id(6L).name("Plank").build();
        
        SetRecord s1 = SetRecord.builder().id(1L).durationSeconds(60).build();
        SetRecord s2 = SetRecord.builder().id(2L).durationSeconds(120).build();

        when(exerciseRepositoryPort.findById(6L)).thenReturn(Optional.of(plank));
        when(setRecordRepositoryPort.findByUserIdAndExerciseId(1L, 6L)).thenReturn(List.of(s1, s2));

        PersonalRecordResponseDto response = useCase.execute(1L, 6L);

        assertNotNull(response);
        assertEquals(6L, response.exerciseId());
        assertNull(response.maxWeight());
        assertNull(response.maxReps());
        assertNull(response.maxVolume());
        assertEquals(120, response.maxDuration());
        assertEquals(2, response.totalSets());
    }

    @Test
    void shouldThrowWhenNoSetsFound() {
        Exercise bench = Exercise.builder().id(5L).name("Bench Press").build();
        when(exerciseRepositoryPort.findById(5L)).thenReturn(Optional.of(bench));
        when(setRecordRepositoryPort.findByUserIdAndExerciseId(1L, 5L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(1L, 5L));
    }

    @Test
    void shouldThrowWhenExerciseNotFound() {
        when(exerciseRepositoryPort.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L, 99L));
    }
}
