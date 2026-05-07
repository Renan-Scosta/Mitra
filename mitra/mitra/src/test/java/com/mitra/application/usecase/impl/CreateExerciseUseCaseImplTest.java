package com.mitra.application.usecase.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.enums.TrackingType;
import com.mitra.presentation.dto.request.CreateExerciseRequestDto;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateExerciseUseCaseImplTest {

    @Mock private ExerciseRepositoryPort exerciseRepositoryPort;

    private CreateExerciseUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateExerciseUseCaseImpl(exerciseRepositoryPort);
    }

    @Test
    void shouldCreateExerciseAndReturnId() {
        CreateExerciseRequestDto request =
                new CreateExerciseRequestDto(
                        "Bench Press", "Chest", new BigDecimal("5.0"), TrackingType.WEIGHT_REPS);

        when(exerciseRepositoryPort.save(any(Exercise.class)))
                .thenReturn(Exercise.builder().id(10L).name("Bench Press").build());

        Long id = useCase.execute(request);

        assertEquals(10L, id);
        verify(exerciseRepositoryPort)
                .save(
                        argThat(
                                e ->
                                        e.getName().equals("Bench Press")
                                                && e.getMuscleGroup().equals("Chest")
                                                && e.getTrackingType()
                                                        == TrackingType.WEIGHT_REPS));
    }
}
