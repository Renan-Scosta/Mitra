package com.mitra.infrastructure.persistence.adapter;

import com.mitra.domain.model.User;
import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.domain.model.enums.Gender;
import com.mitra.infrastructure.persistence.repository.UserJpaRepository;
import com.mitra.infrastructure.persistence.repository.WorkoutRoutineJpaRepository;
import com.mitra.infrastructure.persistence.repository.WorkoutSessionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class WorkoutSessionRepositoryAdapterTest {

    @Autowired
    private WorkoutSessionJpaRepository sessionJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private WorkoutRoutineJpaRepository routineJpaRepository;

    private WorkoutSessionRepositoryAdapter adapter;
    private UserRepositoryAdapter userAdapter;
    private WorkoutRoutineRepositoryAdapter routineAdapter;

    private User savedUser;
    private WorkoutRoutine savedRoutine;

    @BeforeEach
    void setUp() {
        adapter = new WorkoutSessionRepositoryAdapter(sessionJpaRepository, userJpaRepository, routineJpaRepository);
        userAdapter = new UserRepositoryAdapter(userJpaRepository);
        routineAdapter = new WorkoutRoutineRepositoryAdapter(routineJpaRepository, userJpaRepository);

        savedUser = userAdapter.save(User.builder()
                .email("session@mitra.com").name("Session User").password("hashed")
                .birthDate(LocalDate.of(1990, 6, 15)).gender(Gender.MALE).heightCm(180)
                .build());

        savedRoutine = routineAdapter.save(WorkoutRoutine.builder()
                .userId(savedUser.getId()).name("Full Body")
                .build());
    }

    @Test
    void shouldSaveAndFindSessionById() {
        WorkoutSession session = WorkoutSession.builder()
                .userId(savedUser.getId())
                .routineId(savedRoutine.getId())
                .startTime(LocalDateTime.now())
                .build();

        WorkoutSession saved = adapter.save(session);

        assertNotNull(saved.getId());

        Optional<WorkoutSession> found = adapter.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(savedUser.getId(), found.get().getUserId());
        assertEquals(savedRoutine.getId(), found.get().getRoutineId());
    }

    @Test
    void shouldFindSessionsByUserId() {
        adapter.save(WorkoutSession.builder()
                .userId(savedUser.getId()).routineId(savedRoutine.getId())
                .startTime(LocalDateTime.now().minusHours(2)).build());
        adapter.save(WorkoutSession.builder()
                .userId(savedUser.getId()).routineId(savedRoutine.getId())
                .startTime(LocalDateTime.now()).build());

        List<WorkoutSession> sessions = adapter.findByUserId(savedUser.getId());

        assertEquals(2, sessions.size());
    }

    @Test
    void shouldFindActiveSessionByUserId() {
        adapter.save(WorkoutSession.builder()
                .userId(savedUser.getId()).routineId(savedRoutine.getId())
                .startTime(LocalDateTime.now())
                .build());

        Optional<WorkoutSession> active = adapter.findActiveByUserId(savedUser.getId());

        assertTrue(active.isPresent());
        assertTrue(active.get().isActive());
    }

    @Test
    void shouldReturnEmptyWhenNoActiveSession() {
        adapter.save(WorkoutSession.builder()
                .userId(savedUser.getId()).routineId(savedRoutine.getId())
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now())
                .build());

        Optional<WorkoutSession> active = adapter.findActiveByUserId(savedUser.getId());

        assertTrue(active.isEmpty());
    }
}
