package com.mitra.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Period;

import com.mitra.domain.model.enums.Gender;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCalculateAgeCorrectly() {
        User user = User.builder()
                .id(1L)
                .email("test@mitra.com")
                .name("Test")
                .password("x")
                .birthDate(LocalDate.of(2000, 1, 15))
                .gender(Gender.MALE)
                .heightCm(180)
                .build();

        int expectedAge = Period.between(LocalDate.of(2000, 1, 15), LocalDate.now()).getYears();
        assertEquals(expectedAge, user.getAge());
    }

    @Test
    void shouldCalculateAgeForRecentBirthday() {
        LocalDate recentBirthday = LocalDate.now().minusYears(25);
        User user = User.builder()
                .birthDate(recentBirthday)
                .gender(Gender.FEMALE)
                .build();

        assertEquals(25, user.getAge());
    }
}
