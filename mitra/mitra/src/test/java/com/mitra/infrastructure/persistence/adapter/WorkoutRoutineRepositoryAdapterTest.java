package com.mitra.infrastructure.persistence.adapter;

import com.mitra.domain.model.User;
import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.domain.model.enums.Gender;
import com.mitra.infrastructure.persistence.repository.UserJpaRepository;
import com.mitra.infrastructure.persistence.repository.WorkoutRoutineJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class WorkoutRoutineRepositoryAdapterTest {

    @Autowired
    private WorkoutRoutineJpaRepository routineJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private WorkoutRoutineRepositoryAdapter adapter;
    private UserRepositoryAdapter userAdapter;

    @BeforeEach
    void setUp() {
        adapter = new WorkoutRoutineRepositoryAdapter(routineJpaRepository, userJpaRepository);
        userAdapter = new UserRepositoryAdapter(userJpaRepository);
    }

    private User createAndSaveUser() {
        return userAdapter.save(User.builder()
                .email("routine@mitra.com").name("Routine User").password("hashed")
                .birthDate(LocalDate.of(1995, 3, 10)).gender(Gender.MALE).heightCm(175)
                .build());
    }

    @Test
    void shouldSaveAndFindRoutineById() {
        User user = createAndSaveUser();

        WorkoutRoutine routine = WorkoutRoutine.builder()
                .userId(user.getId())
                .name("Push Pull Legs")
                .build();

        WorkoutRoutine saved = adapter.save(routine);

        assertNotNull(saved.getId());
        assertEquals("Push Pull Legs", saved.getName());

        Optional<WorkoutRoutine> found = adapter.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Push Pull Legs", found.get().getName());
    }

    @Test
    void shouldFindRoutinesByUserId() {
        User user = createAndSaveUser();

        adapter.save(WorkoutRoutine.builder().userId(user.getId()).name("Routine A").build());
        adapter.save(WorkoutRoutine.builder().userId(user.getId()).name("Routine B").build());

        List<WorkoutRoutine> routines = adapter.findByUserId(user.getId());

        assertEquals(2, routines.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoRoutinesForUser() {
        List<WorkoutRoutine> routines = adapter.findByUserId(999L);
        assertTrue(routines.isEmpty());
    }

    @Test
    void shouldThrowWhenSavingRoutineForNonExistentUser() {
        WorkoutRoutine routine = WorkoutRoutine.builder()
                .userId(999L).name("Ghost Routine").build();

        assertThrows(IllegalArgumentException.class, () -> adapter.save(routine));
    }
}
