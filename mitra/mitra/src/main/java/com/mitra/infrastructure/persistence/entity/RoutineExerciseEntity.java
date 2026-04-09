package com.mitra.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routine_exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineExerciseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_routine_id", nullable = false)
    private WorkoutRoutineEntity workoutRoutine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private ExerciseEntity exercise;

    @Column(name = "target_sets", nullable = false)
    private Integer targetSets;

    /** Null for TIME_ONLY exercises. */
    @Column(name = "target_reps")
    private Integer targetReps;
}
