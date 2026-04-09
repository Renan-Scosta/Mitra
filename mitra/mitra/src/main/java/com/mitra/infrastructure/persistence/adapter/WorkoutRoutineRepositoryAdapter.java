package com.mitra.infrastructure.persistence.adapter;

import com.mitra.application.port.out.WorkoutRoutineRepositoryPort;
import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.infrastructure.persistence.mapper.WorkoutRoutineMapper;
import com.mitra.infrastructure.persistence.repository.UserJpaRepository;
import com.mitra.infrastructure.persistence.repository.WorkoutRoutineJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<WorkoutRoutine> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(WorkoutRoutineMapper::toDomain)
                .collect(Collectors.toList());
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
