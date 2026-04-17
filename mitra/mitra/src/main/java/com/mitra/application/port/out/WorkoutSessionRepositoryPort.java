package com.mitra.application.port.out;

import com.mitra.domain.model.WorkoutSession;

import java.util.List;
import java.util.Optional;

public interface WorkoutSessionRepositoryPort {
    Optional<WorkoutSession> findById(Long id);
    List<WorkoutSession> findByUserId(Long userId);
    Optional<WorkoutSession> findActiveByUserId(Long userId);
    WorkoutSession save(WorkoutSession session);
}
