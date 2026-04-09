package com.mitra.domain.service;

import com.mitra.domain.model.User;
import com.mitra.domain.model.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BmrCalculator}.
 *
 * No Spring context is loaded — this is a pure domain test.
 */
class BmrCalculatorTest {

    private BmrCalculator bmrCalculator;

    @BeforeEach
    void setUp() {
        bmrCalculator = new BmrCalculator();
    }

    @Test
    @DisplayName("Male | 80 kg | 175 cm | 36 years => BMR = 1718.75 kcal/day")
    void calculate_maleUser_returnsCorrectBmr() {
        // Given — birth date chosen so age is always 36 on April 9, 2026
        User user = User.builder()
                .name("John")
                .birthDate(LocalDate.of(1990, 4, 9))
                .gender(Gender.MALE)
                .heightCm(175)
                .build();

        // When
        double bmr = bmrCalculator.calculate(user, BigDecimal.valueOf(80));

        // Then: (10*80) + (6.25*175) - (5*36) + 5 = 800 + 1093.75 - 180 + 5 = 1718.75
        assertEquals(1718.75, bmr, 0.01,
                "BMR should match Mifflin-St Jeor formula for male");
    }

    @Test
    @DisplayName("Female | 65 kg | 163 cm | 30 years => BMR = 1357.75 kcal/day")
    void calculate_femaleUser_appliesFemaleConstant() {
        // Given — birth date chosen so age is always 30 on April 9, 2026
        User user = User.builder()
                .name("Jane")
                .birthDate(LocalDate.of(1996, 4, 9))
                .gender(Gender.FEMALE)
                .heightCm(163)
                .build();

        // When
        double bmr = bmrCalculator.calculate(user, BigDecimal.valueOf(65));

        // Then: (10*65) + (6.25*163) - (5*30) - 161 = 650 + 1018.75 - 150 - 161 = 1357.75
        assertEquals(1357.75, bmr, 0.01,
                "BMR should apply -161 constant for female");
    }

    @Test
    @DisplayName("Null user should throw IllegalArgumentException")
    void calculate_nullUser_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> bmrCalculator.calculate(null, BigDecimal.valueOf(80)));
    }

    @Test
    @DisplayName("Zero weight should throw IllegalArgumentException")
    void calculate_zeroWeight_throwsException() {
        User user = User.builder()
                .name("Test")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .heightCm(175)
                .build();
        assertThrows(IllegalArgumentException.class,
                () -> bmrCalculator.calculate(user, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Negative weight should throw IllegalArgumentException")
    void calculate_negativeWeight_throwsException() {
        User user = User.builder()
                .name("Test")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .heightCm(175)
                .build();
        assertThrows(IllegalArgumentException.class,
                () -> bmrCalculator.calculate(user, BigDecimal.valueOf(-10)));
    }
}
