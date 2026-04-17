package com.mitra.infrastructure.persistence.adapter;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.domain.model.Exercise;
import com.mitra.infrastructure.persistence.mapper.ExerciseMapper;
import com.mitra.infrastructure.persistence.repository.ExerciseJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    public Page<Exercise> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(ExerciseMapper::toDomain);
    }

    @Override
    public Exercise save(Exercise exercise) {
        var entity = ExerciseMapper.toEntity(exercise);
        var saved = jpaRepository.save(entity);
        return ExerciseMapper.toDomain(saved);
    }
}
