package com.mitra.infrastructure.persistence.adapter;

import com.mitra.domain.model.BodyMeasurement;
import com.mitra.domain.model.User;
import com.mitra.domain.model.enums.Gender;
import com.mitra.infrastructure.persistence.repository.BodyMeasurementJpaRepository;
import com.mitra.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BodyMeasurementRepositoryAdapterTest {

    @Autowired
    private BodyMeasurementJpaRepository bodyMeasurementJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private BodyMeasurementRepositoryAdapter adapter;
    private UserRepositoryAdapter userAdapter;

    @BeforeEach
    void setUp() {
        adapter = new BodyMeasurementRepositoryAdapter(bodyMeasurementJpaRepository, userJpaRepository);
        userAdapter = new UserRepositoryAdapter(userJpaRepository);
    }

    private User createAndSaveUser() {
        User user = User.builder()
                .email("body@mitra.com")
                .name("Body Test User")
                .birthDate(LocalDate.of(1995, 6, 20))
                .gender(Gender.FEMALE)
                .heightCm(165)
                .build();
        return userAdapter.save(user);
    }

    @Test
    void shouldSaveAndFindLatestMeasurementByUserId() {
        User savedUser = createAndSaveUser();

        BodyMeasurement older = BodyMeasurement.builder()
                .userId(savedUser.getId())
                .weightKg(new BigDecimal("65.50"))
                .bodyFatPercentage(new BigDecimal("22.00"))
                .recordDate(LocalDate.of(2025, 1, 1))
                .build();

        BodyMeasurement newer = BodyMeasurement.builder()
                .userId(savedUser.getId())
                .weightKg(new BigDecimal("64.00"))
                .bodyFatPercentage(new BigDecimal("20.50"))
                .recordDate(LocalDate.of(2025, 3, 15))
                .build();

        adapter.save(older);
        BodyMeasurement savedNewer = adapter.save(newer);

        Optional<BodyMeasurement> latest = adapter.findLatestByUserId(savedUser.getId());

        assertTrue(latest.isPresent());
        BodyMeasurement retrieved = latest.get();
        assertEquals(savedNewer.getId(), retrieved.getId());
        assertEquals(savedUser.getId(), retrieved.getUserId());
        assertEquals(0, new BigDecimal("64.00").compareTo(retrieved.getWeightKg()));
        assertEquals(0, new BigDecimal("20.50").compareTo(retrieved.getBodyFatPercentage()));
        assertEquals(LocalDate.of(2025, 3, 15), retrieved.getRecordDate());
    }

    @Test
    void shouldReturnEmptyWhenNoMeasurementExists() {
        User savedUser = createAndSaveUser();
        Optional<BodyMeasurement> found = adapter.findLatestByUserId(savedUser.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldThrowWhenSavingMeasurementForNonExistentUser() {
        BodyMeasurement measurement = BodyMeasurement.builder()
                .userId(999L)
                .weightKg(new BigDecimal("70.00"))
                .recordDate(LocalDate.now())
                .build();

        assertThrows(IllegalArgumentException.class, () -> adapter.save(measurement));
    }
}
