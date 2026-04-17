package com.mitra.infrastructure.persistence.adapter;

import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.enums.TrackingType;
import com.mitra.infrastructure.persistence.repository.ExerciseJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ExerciseRepositoryAdapterTest {

    @Autowired
    private ExerciseJpaRepository exerciseJpaRepository;

    private ExerciseRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ExerciseRepositoryAdapter(exerciseJpaRepository);
    }

    @Test
    void shouldSaveAndFindExerciseById() {
        Exercise exercise = Exercise.builder()
                .name("Squat")
                .muscleGroup("Legs")
                .metFactor(new BigDecimal("8.0"))
                .trackingType(TrackingType.WEIGHT_REPS)
                .build();

        Exercise saved = adapter.save(exercise);

        assertNotNull(saved.getId());
        assertEquals("Squat", saved.getName());

        Optional<Exercise> found = adapter.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Legs", found.get().getMuscleGroup());
        assertEquals(TrackingType.WEIGHT_REPS, found.get().getTrackingType());
    }

    @Test
    void shouldFindAllExercises() {
        adapter.save(Exercise.builder()
                .name("Bench Press").muscleGroup("Chest")
                .metFactor(new BigDecimal("5.0")).trackingType(TrackingType.WEIGHT_REPS).build());
        adapter.save(Exercise.builder()
                .name("Plank").muscleGroup("Core")
                .metFactor(new BigDecimal("3.0")).trackingType(TrackingType.TIME_ONLY).build());

        Page<Exercise> all = adapter.findAll(PageRequest.of(0, 10));

        assertEquals(2, all.getContent().size());
    }

    @Test
    void shouldReturnEmptyWhenExerciseNotFound() {
        Optional<Exercise> found = adapter.findById(999L);
        assertTrue(found.isEmpty());
    }
}
