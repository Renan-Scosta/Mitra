package com.mitra.application.port.out;

import com.mitra.domain.model.Exercise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ExerciseRepositoryPort {
    Optional<Exercise> findById(Long id);
    Page<Exercise> findAll(Pageable pageable);
    Exercise save(Exercise exercise);
}
