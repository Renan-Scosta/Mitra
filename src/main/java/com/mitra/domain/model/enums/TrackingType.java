package com.mitra.domain.model.enums;

public enum TrackingType {
    /** External load exercises (e.g., Bench Press). Requires weight (kg) and reps. */
    WEIGHT_REPS,

    /** Bodyweight exercises (e.g., Push-up). Requires reps only. */
    REPS_ONLY,

    /** Isometric exercises (e.g., Plank). Requires duration in seconds only. */
    TIME_ONLY
}
