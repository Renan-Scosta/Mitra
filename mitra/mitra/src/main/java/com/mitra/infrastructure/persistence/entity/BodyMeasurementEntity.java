package com.mitra.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "body_measurements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyMeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "weight_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "body_fat_percentage", precision = 4, scale = 2)
    private BigDecimal bodyFatPercentage;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;
}
