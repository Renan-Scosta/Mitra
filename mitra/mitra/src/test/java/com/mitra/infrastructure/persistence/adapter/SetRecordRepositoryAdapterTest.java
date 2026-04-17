package com.mitra.infrastructure.persistence.adapter;

import com.mitra.domain.model.*;
import com.mitra.domain.model.enums.Gender;
import com.mitra.domain.model.enums.TrackingType;
import com.mitra.infrastructure.persistence.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SetRecordRepositoryAdapterTest {

    @Autowired private SetRecordJpaRepository setRecordJpaRepository;
    @Autowired private WorkoutSessionJpaRepository sessionJpaRepository;
    @Autowired private ExerciseJpaRepository exerciseJpaRepository;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private WorkoutRoutineJpaRepository routineJpaRepository;

    private SetRecordRepositoryAdapter adapter;
    private WorkoutSession savedSession;
    private Exercise savedExercise;

    @BeforeEach
    void setUp() {
        adapter = new SetRecordRepositoryAdapter(setRecordJpaRepository, sessionJpaRepository, exerciseJpaRepository);

        UserRepositoryAdapter userAdapter = new UserRepositoryAdapter(userJpaRepository);
        ExerciseRepositoryAdapter exerciseAdapter = new ExerciseRepositoryAdapter(exerciseJpaRepository);
        WorkoutRoutineRepositoryAdapter routineAdapter = new WorkoutRoutineRepositoryAdapter(routineJpaRepository, userJpaRepository);
        WorkoutSessionRepositoryAdapter sessionAdapter = new WorkoutSessionRepositoryAdapter(sessionJpaRepository, userJpaRepository, routineJpaRepository);

        User user = userAdapter.save(User.builder()
                .email("set@mitra.com").name("Set User").password("hashed")
                .birthDate(LocalDate.of(1992, 8, 20)).gender(Gender.MALE).heightCm(178)
                .build());

        WorkoutRoutine routine = routineAdapter.save(WorkoutRoutine.builder()
                .userId(user.getId()).name("Test Routine").build());

        savedSession = sessionAdapter.save(WorkoutSession.builder()
                .userId(user.getId()).routineId(routine.getId())
                .startTime(LocalDateTime.now()).build());

        savedExercise = exerciseAdapter.save(Exercise.builder()
                .name("Bench Press").muscleGroup("Chest")
                .metFactor(new BigDecimal("5.0")).trackingType(TrackingType.WEIGHT_REPS).build());
    }

    @Test
    void shouldSaveAndFindBySessionId() {
        SetRecord record = SetRecord.builder()
                .sessionId(savedSession.getId())
                .exercise(savedExercise)
                .weightKg(new BigDecimal("80.00"))
                .reps(10)
                .build();

        SetRecord saved = adapter.save(record);

        assertNotNull(saved.getId());
        assertEquals(10, saved.getReps());

        List<SetRecord> found = adapter.findBySessionId(savedSession.getId());
        assertEquals(1, found.size());
        assertEquals(0, new BigDecimal("80.00").compareTo(found.get(0).getWeightKg()));
    }

    @Test
    void shouldSaveTimeOnlyRecord() {
        Exercise plank = new ExerciseRepositoryAdapter(exerciseJpaRepository).save(
                Exercise.builder().name("Plank").muscleGroup("Core")
                        .metFactor(new BigDecimal("3.0")).trackingType(TrackingType.TIME_ONLY).build());

        SetRecord record = SetRecord.builder()
                .sessionId(savedSession.getId())
                .exercise(plank)
                .durationSeconds(60)
                .build();

        SetRecord saved = adapter.save(record);

        assertNotNull(saved.getId());
        assertEquals(60, saved.getDurationSeconds());
        assertNull(saved.getWeightKg());
        assertNull(saved.getReps());
    }

    @Test
    void shouldReturnEmptyListForSessionWithNoSets() {
        List<SetRecord> found = adapter.findBySessionId(savedSession.getId());
        assertTrue(found.isEmpty());
    }
}
