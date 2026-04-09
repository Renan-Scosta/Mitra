package com.mitra.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRoutine {

    private Long id;
    private Long userId;
    private String name;

    @Builder.Default
    private List<RoutineExercise> routineExercises = new ArrayList<>();
}
