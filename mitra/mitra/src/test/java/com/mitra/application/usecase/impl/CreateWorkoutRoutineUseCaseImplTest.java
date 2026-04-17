package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutRoutineRepositoryPort;
import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.presentation.dto.request.CreateRoutineRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateWorkoutRoutineUseCaseImplTest {

    @Mock
    private WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort;

    private CreateWorkoutRoutineUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateWorkoutRoutineUseCaseImpl(workoutRoutineRepositoryPort);
    }

    @Test
    void shouldCreateRoutineForAuthenticatedUser() {
        CreateRoutineRequestDto request = new CreateRoutineRequestDto("Push Pull Legs A");

        when(workoutRoutineRepositoryPort.save(any(WorkoutRoutine.class)))
                .thenReturn(WorkoutRoutine.builder().id(5L).userId(1L).name("Push Pull Legs A").build());

        Long id = useCase.execute(1L, request);

        assertEquals(5L, id);
        verify(workoutRoutineRepositoryPort).save(argThat(r ->
                r.getUserId().equals(1L) && r.getName().equals("Push Pull Legs A")
        ));
    }
}
