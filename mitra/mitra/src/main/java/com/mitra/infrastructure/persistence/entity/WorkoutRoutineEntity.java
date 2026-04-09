package com.mitra.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_routines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutRoutineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "workoutRoutine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoutineExerciseEntity> routineExercises = new ArrayList<>();

    @OneToMany(mappedBy = "workoutRoutine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkoutSessionEntity> workoutSessions = new ArrayList<>();
}
