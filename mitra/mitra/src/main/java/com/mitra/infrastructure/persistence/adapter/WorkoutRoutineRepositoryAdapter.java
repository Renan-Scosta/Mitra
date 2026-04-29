package com.mitra.infrastructure.persistence.adapter;

import com.mitra.application.port.out.WorkoutRoutineRepositoryPort;
import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.infrastructure.persistence.mapper.WorkoutRoutineMapper;
import com.mitra.infrastructure.persistence.repository.UserJpaRepository;
import com.mitra.infrastructure.persistence.repository.WorkoutRoutineJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class WorkoutRoutineRepositoryAdapter implements WorkoutRoutineRepositoryPort {

    private final WorkoutRoutineJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;

    public WorkoutRoutineRepositoryAdapter(
            WorkoutRoutineJpaRepository jpaRepository,
            UserJpaRepository userJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<WorkoutRoutine> findById(Long id) {
        return jpaRepository.findById(id)
                .map(WorkoutRoutineMapper::toDomain);
    }

    @Override
    public Page<WorkoutRoutine> findByUserId(Long userId, Pageable pageable) {
        return jpaRepository.findByUserId(userId, pageable)
                .map(WorkoutRoutineMapper::toDomain);
    }

    @Override
    public WorkoutRoutine save(WorkoutRoutine routine) {
        var userEntity = userJpaRepository.findById(routine.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + routine.getUserId()));
        var entity = WorkoutRoutineMapper.toEntity(routine, userEntity);
        var saved = jpaRepository.save(entity);
        return WorkoutRoutineMapper.toDomain(saved);
    }
}
