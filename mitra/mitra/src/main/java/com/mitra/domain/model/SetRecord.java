package com.mitra.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetRecord {

    private Long id;
    private Long sessionId;
    private Exercise exercise;

    /** Null for REPS_ONLY and TIME_ONLY exercises. */
    private BigDecimal weightKg;

    /** Null for TIME_ONLY exercises. */
    private Integer reps;

    /** Null for WEIGHT_REPS and REPS_ONLY exercises. */
    private Integer durationSeconds;
}
