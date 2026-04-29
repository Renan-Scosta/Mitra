package com.mitra.application.port.out;

import com.mitra.domain.model.WorkoutRoutine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface WorkoutRoutineRepositoryPort {
    Optional<WorkoutRoutine> findById(Long id);
    Page<WorkoutRoutine> findByUserId(Long userId, Pageable pageable);
    WorkoutRoutine save(WorkoutRoutine routine);
}
