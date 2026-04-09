package com.mitra.infrastructure.persistence.adapter;

import com.mitra.application.port.out.SetRecordRepositoryPort;
import com.mitra.domain.model.SetRecord;
import com.mitra.infrastructure.persistence.mapper.SetRecordMapper;
import com.mitra.infrastructure.persistence.repository.ExerciseJpaRepository;
import com.mitra.infrastructure.persistence.repository.SetRecordJpaRepository;
import com.mitra.infrastructure.persistence.repository.WorkoutSessionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SetRecordRepositoryAdapter implements SetRecordRepositoryPort {

    private final SetRecordJpaRepository jpaRepository;
    private final WorkoutSessionJpaRepository sessionJpaRepository;
    private final ExerciseJpaRepository exerciseJpaRepository;

    public SetRecordRepositoryAdapter(
            SetRecordJpaRepository jpaRepository,
            WorkoutSessionJpaRepository sessionJpaRepository,
            ExerciseJpaRepository exerciseJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.sessionJpaRepository = sessionJpaRepository;
        this.exerciseJpaRepository = exerciseJpaRepository;
    }

    @Override
    public List<SetRecord> findBySessionId(Long sessionId) {
        return jpaRepository.findByWorkoutSessionId(sessionId).stream()
                .map(SetRecordMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public SetRecord save(SetRecord setRecord) {
        var sessionEntity = sessionJpaRepository.findById(setRecord.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Workout session not found: " + setRecord.getSessionId()));
        var exerciseEntity = exerciseJpaRepository.findById(setRecord.getExercise().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Exercise not found: " + setRecord.getExercise().getId()));
        var entity = SetRecordMapper.toEntity(setRecord, sessionEntity, exerciseEntity);
        var saved = jpaRepository.save(entity);
        return SetRecordMapper.toDomain(saved);
    }
}
