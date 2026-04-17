package com.mitra.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class WorkoutSessionTest {

    @Test
    void shouldBeActiveWhenEndTimeIsNull() {
        WorkoutSession session = WorkoutSession.builder()
                .id(1L)
                .userId(1L)
                .routineId(1L)
                .startTime(LocalDateTime.now())
                .build();

        assertTrue(session.isActive());
    }

    @Test
    void shouldNotBeActiveAfterFinish() {
        WorkoutSession session = WorkoutSession.builder()
                .id(1L)
                .userId(1L)
                .routineId(1L)
                .startTime(LocalDateTime.now().minusMinutes(30))
                .build();

        session.finish();

        assertFalse(session.isActive());
        assertNotNull(session.getEndTime());
    }

    @Test
    void shouldThrowWhenFinishingAlreadyFinishedSession() {
        WorkoutSession session = WorkoutSession.builder()
                .id(1L)
                .userId(1L)
                .routineId(1L)
                .startTime(LocalDateTime.now().minusMinutes(30))
                .endTime(LocalDateTime.now())
                .build();

        assertThrows(IllegalStateException.class, session::finish);
    }

    @Test
    void shouldNotBeAbandonedWhenUnderThreshold() {
        WorkoutSession session = WorkoutSession.builder()
                .id(1L)
                .userId(1L)
                .routineId(1L)
                .startTime(LocalDateTime.now().minusHours(1))
                .build();

        assertFalse(session.isAbandoned());
    }

    @Test
    void shouldBeAbandonedWhenOverThreeHours() {
        WorkoutSession session = WorkoutSession.builder()
                .id(1L)
                .userId(1L)
                .routineId(1L)
                .startTime(LocalDateTime.now().minusHours(4))
                .build();

        assertTrue(session.isAbandoned());
    }

    @Test
    void shouldNotBeAbandonedWhenAlreadyFinished() {
        WorkoutSession session = WorkoutSession.builder()
                .id(1L)
                .userId(1L)
                .routineId(1L)
                .startTime(LocalDateTime.now().minusHours(5))
                .endTime(LocalDateTime.now())
                .build();

        assertFalse(session.isAbandoned());
    }

    @Test
    void shouldReturnActualDurationWhenUnderThreshold() {
        LocalDateTime start = LocalDateTime.now().minusMinutes(45);
        WorkoutSession session = WorkoutSession.builder()
                .id(1L)
                .userId(1L)
                .routineId(1L)
                .startTime(start)
                .endTime(start.plusMinutes(45))
                .build();

        Duration effective = session.getEffectiveDuration();
        assertEquals(45, effective.toMinutes());
    }

    @Test
    void shouldCapDurationAt60MinutesWhenAbandoned() {
        WorkoutSession session = WorkoutSession.builder()
                .id(1L)
                .userId(1L)
                .routineId(1L)
                .startTime(LocalDateTime.now().minusHours(5))
                .endTime(LocalDateTime.now())
                .build();

        Duration effective = session.getEffectiveDuration();
        assertEquals(60, effective.toMinutes());
    }

    @Test
    void shouldCapDurationAt60MinutesForActiveAbandonedSession() {
        WorkoutSession session = WorkoutSession.builder()
                .id(1L)
                .userId(1L)
                .routineId(1L)
                .startTime(LocalDateTime.now().minusHours(4))
                .build();

        Duration effective = session.getEffectiveDuration();
        assertEquals(60, effective.toMinutes());
    }
}
