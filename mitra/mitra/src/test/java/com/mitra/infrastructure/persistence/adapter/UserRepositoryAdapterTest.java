package com.mitra.infrastructure.persistence.adapter;

import com.mitra.domain.model.User;
import com.mitra.domain.model.enums.Gender;
import com.mitra.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryAdapterTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    private UserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserRepositoryAdapter(userJpaRepository);
    }

    @Test
    void shouldSaveAndFindUserById() {
        User user = User.builder()
                .email("test@mitra.com")
                .name("Test User")
                .birthDate(LocalDate.of(2000, 1, 15))
                .gender(Gender.MALE)
                .heightCm(180)
                .build();

        User saved = adapter.save(user);

        assertNotNull(saved.getId());
        assertEquals("test@mitra.com", saved.getEmail());
        assertEquals("Test User", saved.getName());
        assertEquals(LocalDate.of(2000, 1, 15), saved.getBirthDate());
        assertEquals(Gender.MALE, saved.getGender());
        assertEquals(180, saved.getHeightCm());

        Optional<User> found = adapter.findById(saved.getId());

        assertTrue(found.isPresent());
        User retrieved = found.get();
        assertEquals(saved.getId(), retrieved.getId());
        assertEquals("test@mitra.com", retrieved.getEmail());
        assertEquals("Test User", retrieved.getName());
        assertEquals(LocalDate.of(2000, 1, 15), retrieved.getBirthDate());
        assertEquals(Gender.MALE, retrieved.getGender());
        assertEquals(180, retrieved.getHeightCm());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> found = adapter.findById(999L);
        assertTrue(found.isEmpty());
    }
}
