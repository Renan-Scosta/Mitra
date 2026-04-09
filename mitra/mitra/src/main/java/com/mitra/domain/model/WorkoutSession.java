package com.mitra.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSession {

    /**
     * A session open for more than 3 hours is considered abandoned.
     */
    private static final Duration ABANDONED_THRESHOLD = Duration.ofHours(3);

    /**
     * For abandoned sessions, caloric expenditure is computed using 60 minutes, not the real duration.
     */
    private static final Duration ABANDONED_CALORIE_WINDOW = Duration.ofMinutes(60);

    private Long id;
    private Long routineId;
    private Long userId;
    private LocalDateTime startTime;

    /** Null while the session is in progress. */
    private LocalDateTime endTime;

    @Builder.Default
    private List<SetRecord> setRecords = new ArrayList<>();

    public boolean isActive() {
        return endTime == null;
    }

    public boolean isAbandoned() {
        if (!isActive()) return false;
        return Duration.between(startTime, LocalDateTime.now()).compareTo(ABANDONED_THRESHOLD) >= 0;
    }

    /**
     * Returns the effective duration for caloric expenditure calculations.
     * If the real duration exceeds 3 hours, caps it at 60 minutes (abandoned session rule).
     */
    public Duration getEffectiveDuration() {
        LocalDateTime end = endTime != null ? endTime : LocalDateTime.now();
        Duration actual = Duration.between(startTime, end);
        return actual.compareTo(ABANDONED_THRESHOLD) >= 0 ? ABANDONED_CALORIE_WINDOW : actual;
    }
}
