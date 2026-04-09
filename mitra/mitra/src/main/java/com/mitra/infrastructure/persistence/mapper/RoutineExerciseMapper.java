package com.mitra.infrastructure.persistence.mapper;

import com.mitra.domain.model.RoutineExercise;
import com.mitra.infrastructure.persistence.entity.ExerciseEntity;
import com.mitra.infrastructure.persistence.entity.RoutineExerciseEntity;
import com.mitra.infrastructure.persistence.entity.WorkoutRoutineEntity;

public final class RoutineExerciseMapper {

    private RoutineExerciseMapper() {}

    public static RoutineExercise toDomain(RoutineExerciseEntity entity) {
        return RoutineExercise.builder()
                .id(entity.getId())
                .routineId(entity.getWorkoutRoutine().getId())
                .exercise(ExerciseMapper.toDomain(entity.getExercise()))
                .targetSets(entity.getTargetSets())
                .targetReps(entity.getTargetReps())
                .build();
    }

    public static RoutineExerciseEntity toEntity(
            RoutineExercise domain,
            WorkoutRoutineEntity routineEntity,
            ExerciseEntity exerciseEntity) {
        return RoutineExerciseEntity.builder()
                .id(domain.getId())
                .workoutRoutine(routineEntity)
                .exercise(exerciseEntity)
                .targetSets(domain.getTargetSets())
                .targetReps(domain.getTargetReps())
                .build();
    }
}
