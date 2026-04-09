package com.mitra.domain.model;

import com.mitra.domain.model.enums.TrackingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

    private Long id;
    private String name;
    private String muscleGroup;
    private BigDecimal metFactor;
    private TrackingType trackingType;
}
