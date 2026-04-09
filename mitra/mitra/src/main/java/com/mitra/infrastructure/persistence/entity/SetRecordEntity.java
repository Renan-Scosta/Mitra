package com.mitra.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "set_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_session_id", nullable = false)
    private WorkoutSessionEntity workoutSession;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private ExerciseEntity exercise;

    /** Populated for WEIGHT_REPS. Null for REPS_ONLY and TIME_ONLY. */
    @Column(name = "weight_kg", precision = 6, scale = 2)
    private BigDecimal weightKg;

    /** Populated for WEIGHT_REPS and REPS_ONLY. Null for TIME_ONLY. */
    private Integer reps;

    /** Populated for TIME_ONLY. Null for WEIGHT_REPS and REPS_ONLY. */
    @Column(name = "duration_seconds")
    private Integer durationSeconds;
}
