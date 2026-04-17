package com.mitra.application.port.out;

import com.mitra.domain.model.RoutineExercise;

import java.util.List;

public interface RoutineExerciseRepositoryPort {
    List<RoutineExercise> findByRoutineId(Long routineId);
    RoutineExercise save(RoutineExercise routineExercise);
}
