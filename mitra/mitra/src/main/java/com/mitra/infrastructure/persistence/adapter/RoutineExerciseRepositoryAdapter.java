package com.mitra.infrastructure.persistence.adapter;

import com.mitra.application.port.out.RoutineExerciseRepositoryPort;
import com.mitra.domain.model.RoutineExercise;
import com.mitra.infrastructure.persistence.mapper.RoutineExerciseMapper;
import com.mitra.infrastructure.persistence.repository.ExerciseJpaRepository;
import com.mitra.infrastructure.persistence.repository.RoutineExerciseJpaRepository;
import com.mitra.infrastructure.persistence.repository.WorkoutRoutineJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RoutineExerciseRepositoryAdapter implements RoutineExerciseRepositoryPort {

    private final RoutineExerciseJpaRepository jpaRepository;
    private final WorkoutRoutineJpaRepository routineJpaRepository;
    private final ExerciseJpaRepository exerciseJpaRepository;

    public RoutineExerciseRepositoryAdapter(
            RoutineExerciseJpaRepository jpaRepository,
            WorkoutRoutineJpaRepository routineJpaRepository,
            ExerciseJpaRepository exerciseJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.routineJpaRepository = routineJpaRepository;
        this.exerciseJpaRepository = exerciseJpaRepository;
    }

    @Override
    public List<RoutineExercise> findByRoutineId(Long routineId) {
        return jpaRepository.findByWorkoutRoutineId(routineId).stream()
                .map(RoutineExerciseMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public RoutineExercise save(RoutineExercise routineExercise) {
        var routineEntity = routineJpaRepository.findById(routineExercise.getRoutineId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Workout routine not found: " + routineExercise.getRoutineId()));
        var exerciseEntity = exerciseJpaRepository.findById(routineExercise.getExercise().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Exercise not found: " + routineExercise.getExercise().getId()));
        var entity = RoutineExerciseMapper.toEntity(routineExercise, routineEntity, exerciseEntity);
        var saved = jpaRepository.save(entity);
        return RoutineExerciseMapper.toDomain(saved);
    }
}
