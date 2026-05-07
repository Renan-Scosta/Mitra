package com.mitra.application.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.SetRecord;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.VolumeSummaryResponseDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUserVolumeSummaryUseCaseImplTest {

    @Mock private WorkoutSessionRepositoryPort sessionRepositoryPort;

    private GetUserVolumeSummaryUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetUserVolumeSummaryUseCaseImpl(sessionRepositoryPort);
    }

    @Test
    void shouldReturnVolumeSummaryByMuscleGroup() {
        LocalDateTime now = LocalDateTime.now();
        Exercise benchPress =
                Exercise.builder().id(1L).name("Bench Press").muscleGroup("Chest").build();
        Exercise squat = Exercise.builder().id(2L).name("Squat").muscleGroup("Legs").build();

        SetRecord set1 =
                SetRecord.builder()
                        .exercise(benchPress)
                        .weightKg(new BigDecimal("100"))
                        .reps(10)
                        .build();
        SetRecord set2 =
                SetRecord.builder().exercise(squat).weightKg(new BigDecimal("120")).reps(5).build();

        WorkoutSession session =
                WorkoutSession.builder()
                        .id(1L)
                        .userId(1L)
                        .startTime(now)
                        .endTime(now.plusHours(1))
                        .setRecords(List.of(set1, set2))
                        .build();

        when(sessionRepositoryPort.findByUserId(1L)).thenReturn(List.of(session));

        List<VolumeSummaryResponseDto> summary =
                useCase.execute(1L, now.minusDays(7), now.plusDays(1));

        assertFalse(summary.isEmpty());
        assertEquals(2, summary.size());

        VolumeSummaryResponseDto chestVol =
                summary.stream().filter(v -> v.muscleGroup().equals("Chest")).findFirst().get();
        assertEquals(1000.0, chestVol.totalVolumeKg());

        VolumeSummaryResponseDto legsVol =
                summary.stream().filter(v -> v.muscleGroup().equals("Legs")).findFirst().get();
        assertEquals(600.0, legsVol.totalVolumeKg());
    }
}
