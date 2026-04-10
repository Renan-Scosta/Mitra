package com.mitra.application.usecase.impl;

import com.mitra.application.usecase.LogSetRecordUseCase;
import com.mitra.presentation.dto.request.LogSetRequestDto;
import com.mitra.presentation.dto.response.SetRecordResponseDto;
import org.springframework.stereotype.Service;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.application.port.out.SetRecordRepositoryPort;
import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.SetRecord;
import com.mitra.domain.model.WorkoutSession;

@Service
public class LogSetRecordUseCaseImpl implements LogSetRecordUseCase {

    private final WorkoutSessionRepositoryPort workoutSessionRepositoryPort;
    private final ExerciseRepositoryPort exerciseRepositoryPort;
    private final SetRecordRepositoryPort setRecordRepositoryPort;

    public LogSetRecordUseCaseImpl(WorkoutSessionRepositoryPort workoutSessionRepositoryPort,
                                   ExerciseRepositoryPort exerciseRepositoryPort,
                                   SetRecordRepositoryPort setRecordRepositoryPort) {
        this.workoutSessionRepositoryPort = workoutSessionRepositoryPort;
        this.exerciseRepositoryPort = exerciseRepositoryPort;
        this.setRecordRepositoryPort = setRecordRepositoryPort;
    }

    @Override
    public SetRecordResponseDto execute(Long sessionId, LogSetRequestDto request) {
        WorkoutSession session = workoutSessionRepositoryPort.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
                
        if (!session.isActive()) {
            throw new IllegalStateException("Session is no longer active");
        }
        
        Exercise exercise = exerciseRepositoryPort.findById(request.exerciseId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
                
        SetRecord record = SetRecord.builder()
                .sessionId(sessionId)
                .exercise(exercise)
                .weightKg(request.weightKg())
                .reps(request.reps())
                .durationSeconds(request.durationSeconds())
                .build();
                
        SetRecord saved = setRecordRepositoryPort.save(record);
        
        return new SetRecordResponseDto(
                saved.getId(),
                saved.getExercise().getId(),
                saved.getWeightKg(),
                saved.getReps(),
                saved.getDurationSeconds()
        );
    }
}
