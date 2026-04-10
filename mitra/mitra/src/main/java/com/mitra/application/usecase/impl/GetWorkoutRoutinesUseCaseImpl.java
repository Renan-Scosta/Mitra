package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutRoutineRepositoryPort;
import com.mitra.application.usecase.GetWorkoutRoutinesUseCase;
import com.mitra.presentation.dto.response.RoutineResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetWorkoutRoutinesUseCaseImpl implements GetWorkoutRoutinesUseCase {

    private final WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort;

    public GetWorkoutRoutinesUseCaseImpl(WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort) {
        this.workoutRoutineRepositoryPort = workoutRoutineRepositoryPort;
    }

    @Override
    public List<RoutineResponseDto> execute(Long userId) {
        return workoutRoutineRepositoryPort.findByUserId(userId).stream()
                .map(routine -> new RoutineResponseDto(
                        routine.getId(),
                        routine.getUserId(),
                        routine.getName()
                ))
                .collect(Collectors.toList());
    }
}
