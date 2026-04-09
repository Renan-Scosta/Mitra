package com.mitra.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineExercise {

    private Long id;
    private Long routineId;
    private Exercise exercise;
    private int targetSets;

    /**
     * Target repetitions. Null for TIME_ONLY exercises.
     */
    private Integer targetReps;
}
