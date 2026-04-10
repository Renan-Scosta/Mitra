package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutRoutineRepositoryPort;
import com.mitra.application.usecase.GetWorkoutRoutinesUseCase;
import com.mitra.presentation.dto.response.RoutineResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import com.mitra.presentation.dto.response.ExerciseResponseDto;
import com.mitra.presentation.dto.response.RoutineExerciseResponseDto;
import java.util.ArrayList;

@Service
public class GetWorkoutRoutinesUseCaseImpl implements GetWorkoutRoutinesUseCase {

    private final WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort;

    public GetWorkoutRoutinesUseCaseImpl(WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort) {
        this.workoutRoutineRepositoryPort = workoutRoutineRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoutineResponseDto> execute(Long userId) {
        return workoutRoutineRepositoryPort.findByUserId(userId).stream()
                .map(routine -> {
                    List<RoutineExerciseResponseDto> exerciseDtos = new ArrayList<>();
                    if (routine.getRoutineExercises() != null) {
                        exerciseDtos = routine.getRoutineExercises().stream()
                                .map(re -> new RoutineExerciseResponseDto(
                                        re.getId(),
                                        new ExerciseResponseDto(
                                                re.getExercise().getId(),
                                                re.getExercise().getName(),
                                                re.getExercise().getMuscleGroup(),
                                                re.getExercise().getMetFactor(),
                                                re.getExercise().getTrackingType()
                                        ),
                                        re.getTargetSets(),
                                        re.getTargetReps()
                                ))
                                .collect(Collectors.toList());
                    }
                    
                    return new RoutineResponseDto(
                            routine.getId(),
                            routine.getUserId(),
                            routine.getName(),
                            exerciseDtos
                    );
                })
                .collect(Collectors.toList());    }
}
