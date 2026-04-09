package com.mitra.infrastructure.persistence.adapter;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.domain.model.Exercise;
import com.mitra.infrastructure.persistence.mapper.ExerciseMapper;
import com.mitra.infrastructure.persistence.repository.ExerciseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ExerciseRepositoryAdapter implements ExerciseRepositoryPort {

    private final ExerciseJpaRepository jpaRepository;

    public ExerciseRepositoryAdapter(ExerciseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Exercise> findById(Long id) {
        return jpaRepository.findById(id)
                .map(ExerciseMapper::toDomain);
    }

    @Override
    public List<Exercise> findAll() {
        return jpaRepository.findAll().stream()
                .map(ExerciseMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Exercise save(Exercise exercise) {
        var entity = ExerciseMapper.toEntity(exercise);
        var saved = jpaRepository.save(entity);
        return ExerciseMapper.toDomain(saved);
    }
}
