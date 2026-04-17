package com.mitra.application.port.out;

import com.mitra.domain.model.WorkoutSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkoutSessionRepositoryPort {
    Optional<WorkoutSession> findById(Long id);
    List<WorkoutSession> findByUserId(Long userId);
    Page<WorkoutSession> findByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Optional<WorkoutSession> findActiveByUserId(Long userId);
    WorkoutSession save(WorkoutSession session);
}
