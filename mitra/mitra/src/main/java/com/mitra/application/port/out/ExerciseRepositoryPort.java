package com.mitra.application.port.out;

import com.mitra.domain.model.Exercise;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepositoryPort {
    Optional<Exercise> findById(Long id);
    List<Exercise> findAll();
    Exercise save(Exercise exercise);
}
