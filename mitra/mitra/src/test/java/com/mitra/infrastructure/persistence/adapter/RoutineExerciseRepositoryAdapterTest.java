package com.mitra.infrastructure.persistence.adapter;

import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.RoutineExercise;
import com.mitra.domain.model.User;
import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.domain.model.enums.Gender;
import com.mitra.domain.model.enums.TrackingType;
import com.mitra.infrastructure.persistence.repository.ExerciseJpaRepository;
import com.mitra.infrastructure.persistence.repository.RoutineExerciseJpaRepository;
import com.mitra.infrastructure.persistence.repository.UserJpaRepository;
import com.mitra.infrastructure.persistence.repository.WorkoutRoutineJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RoutineExerciseRepositoryAdapterTest {

    @Autowired
    private RoutineExerciseJpaRepository routineExerciseJpaRepository;

    @Autowired
    private WorkoutRoutineJpaRepository routineJpaRepository;

    @Autowired
    private ExerciseJpaRepository exerciseJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private RoutineExerciseRepositoryAdapter adapter;
    private ExerciseRepositoryAdapter exerciseAdapter;
    private WorkoutRoutineRepositoryAdapter routineAdapter;
    private UserRepositoryAdapter userAdapter;

    private WorkoutRoutine savedRoutine;
    private Exercise savedExercise;

    @BeforeEach
    void setUp() {
        adapter = new RoutineExerciseRepositoryAdapter(routineExerciseJpaRepository, routineJpaRepository, exerciseJpaRepository);
        exerciseAdapter = new ExerciseRepositoryAdapter(exerciseJpaRepository);
        routineAdapter = new WorkoutRoutineRepositoryAdapter(routineJpaRepository, userJpaRepository);
        userAdapter = new UserRepositoryAdapter(userJpaRepository);

        User user = userAdapter.save(User.builder()
                .email("re@mitra.com").name("RE User").password("hashed")
                .birthDate(LocalDate.of(1995, 1, 1)).gender(Gender.MALE).heightCm(175)
                .build());

        savedRoutine = routineAdapter.save(WorkoutRoutine.builder()
                .userId(user.getId()).name("Test Routine").build());

        savedExercise = exerciseAdapter.save(Exercise.builder()
                .name("Squat").muscleGroup("Legs")
                .metFactor(new BigDecimal("8.0")).trackingType(TrackingType.WEIGHT_REPS).build());
    }

    @Test
    void shouldSaveAndFindByRoutineId() {
        RoutineExercise re = RoutineExercise.builder()
                .routineId(savedRoutine.getId())
                .exercise(savedExercise)
                .targetSets(4)
                .targetReps(10)
                .build();

        RoutineExercise saved = adapter.save(re);

        assertNotNull(saved.getId());
        assertEquals(4, saved.getTargetSets());

        List<RoutineExercise> found = adapter.findByRoutineId(savedRoutine.getId());
        assertEquals(1, found.size());
        assertEquals("Squat", found.get(0).getExercise().getName());
    }

    @Test
    void shouldReturnEmptyListForRoutineWithNoExercises() {
        List<RoutineExercise> found = adapter.findByRoutineId(savedRoutine.getId());
        assertTrue(found.isEmpty());
    }
}
