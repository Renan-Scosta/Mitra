package com.mitra.application.port.out;

import com.mitra.domain.model.WorkoutRoutine;

import java.util.List;
import java.util.Optional;

public interface WorkoutRoutineRepositoryPort {
    Optional<WorkoutRoutine> findById(Long id);
    List<WorkoutRoutine> findByUserId(Long userId);
    WorkoutRoutine save(WorkoutRoutine routine);
}
