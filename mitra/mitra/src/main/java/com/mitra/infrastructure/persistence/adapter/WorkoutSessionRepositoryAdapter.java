package com.mitra.infrastructure.persistence.adapter;

import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.infrastructure.persistence.mapper.WorkoutSessionMapper;
import com.mitra.infrastructure.persistence.repository.UserJpaRepository;
import com.mitra.infrastructure.persistence.repository.WorkoutRoutineJpaRepository;
import com.mitra.infrastructure.persistence.repository.WorkoutSessionJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class WorkoutSessionRepositoryAdapter implements WorkoutSessionRepositoryPort {

    private final WorkoutSessionJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final WorkoutRoutineJpaRepository routineJpaRepository;

    public WorkoutSessionRepositoryAdapter(
            WorkoutSessionJpaRepository jpaRepository,
            UserJpaRepository userJpaRepository,
            WorkoutRoutineJpaRepository routineJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.routineJpaRepository = routineJpaRepository;
    }

    @Override
    public Optional<WorkoutSession> findById(Long id) {
        return jpaRepository.findById(id)
                .map(WorkoutSessionMapper::toDomain);
    }

    @Override
    public List<WorkoutSession> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(WorkoutSessionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<WorkoutSession> findByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return jpaRepository.findByUserIdAndStartTimeBetween(userId, start, end, pageable)
                .map(WorkoutSessionMapper::toDomain);
    }

    @Override
    public Optional<WorkoutSession> findActiveByUserId(Long userId) {
        return jpaRepository.findByUserIdAndEndTimeIsNull(userId)
                .map(WorkoutSessionMapper::toDomain);
    }

    @Override
    public WorkoutSession save(WorkoutSession session) {
        var userEntity = userJpaRepository.findById(session.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + session.getUserId()));
        var routineEntity = routineJpaRepository.findById(session.getRoutineId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Workout routine not found: " + session.getRoutineId()));
        var entity = WorkoutSessionMapper.toEntity(session, userEntity, routineEntity);
        var saved = jpaRepository.save(entity);
        return WorkoutSessionMapper.toDomain(saved);
    }
}
