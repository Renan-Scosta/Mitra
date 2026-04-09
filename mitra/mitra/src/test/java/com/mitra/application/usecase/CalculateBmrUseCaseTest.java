package com.mitra.application.usecase;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.domain.model.BodyMeasurement;
import com.mitra.domain.model.User;
import com.mitra.domain.model.enums.Gender;
import com.mitra.domain.service.BmrCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CalculateBmrUseCase}.
 *
 * Demonstrates JUnit 5 + Mockito: repository ports are mocked so the test
 * verifies only the use case orchestration logic.
 */
@ExtendWith(MockitoExtension.class)
class CalculateBmrUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private BodyMeasurementRepositoryPort bodyMeasurementRepository;

    private CalculateBmrUseCase calculateBmrUseCase;

    @BeforeEach
    void setUp() {
        // BmrCalculator is a pure domain object — no need to mock it
        calculateBmrUseCase = new CalculateBmrUseCase(
                userRepository,
                bodyMeasurementRepository,
                new BmrCalculator()
        );
    }

    @Test
    @DisplayName("execute: valid user with measurement returns correct BMR")
    void execute_validUser_returnsBmr() {
        // Given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("John")
                .birthDate(LocalDate.of(1990, 4, 9))
                .gender(Gender.MALE)
                .heightCm(175)
                .build();
        BodyMeasurement measurement = BodyMeasurement.builder()
                .userId(userId)
                .weightKg(BigDecimal.valueOf(80))
                .recordDate(LocalDate.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bodyMeasurementRepository.findLatestByUserId(userId)).thenReturn(Optional.of(measurement));

        // When
        double bmr = calculateBmrUseCase.execute(userId);

        // Then — 10*80 + 6.25*175 - 5*36 + 5 = 1718.75
        assertEquals(1718.75, bmr, 0.01);
        verify(userRepository, times(1)).findById(userId);
        verify(bodyMeasurementRepository, times(1)).findLatestByUserId(userId);
    }

    @Test
    @DisplayName("execute: user not found throws IllegalArgumentException")
    void execute_userNotFound_throwsException() {
        // Given
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> calculateBmrUseCase.execute(userId));

        // Body measurement repository should never be queried
        verify(bodyMeasurementRepository, never()).findLatestByUserId(any());
    }

    @Test
    @DisplayName("execute: no body measurement throws IllegalArgumentException")
    void execute_noBodyMeasurement_throwsException() {
        // Given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("Jane")
                .birthDate(LocalDate.of(1996, 4, 9))
                .gender(Gender.FEMALE)
                .heightCm(163)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bodyMeasurementRepository.findLatestByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> calculateBmrUseCase.execute(userId));
    }
}
