package com.mitra.application.port.out;

import com.mitra.domain.model.WorkoutRoutine;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkoutRoutineRepositoryPort {
    Optional<WorkoutRoutine> findById(Long id);

    Page<WorkoutRoutine> findByUserId(Long userId, Pageable pageable);

    WorkoutRoutine save(WorkoutRoutine routine);
}
