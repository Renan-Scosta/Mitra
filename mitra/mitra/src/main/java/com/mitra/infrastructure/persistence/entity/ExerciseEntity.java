package com.mitra.infrastructure.persistence.entity;

import com.mitra.domain.model.enums.TrackingType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "muscle_group", nullable = false)
    private String muscleGroup;

    @Column(name = "met_factor", nullable = false, precision = 4, scale = 1)
    private BigDecimal metFactor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tracking_type", nullable = false, length = 12)
    private TrackingType trackingType;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoutineExerciseEntity> routineExercises = new ArrayList<>();

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SetRecordEntity> setRecords = new ArrayList<>();
}
