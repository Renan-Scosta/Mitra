package com.mitra.presentation.controller;

import com.mitra.application.usecase.LogSetRecordUseCase;
import com.mitra.application.usecase.StartWorkoutSessionUseCase;
import com.mitra.presentation.dto.request.LogSetRequestDto;
import com.mitra.presentation.dto.request.StartSessionRequestDto;
import com.mitra.presentation.dto.response.SetRecordResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Workout Sessions", description = "Endpoints for executing and tracking workout routines")
@RestController
@RequestMapping("/api/v1/sessions")
public class WorkoutSessionController {

    private final StartWorkoutSessionUseCase startWorkoutSessionUseCase;
    private final LogSetRecordUseCase logSetRecordUseCase;

    public WorkoutSessionController(StartWorkoutSessionUseCase startWorkoutSessionUseCase,
                                    LogSetRecordUseCase logSetRecordUseCase) {
        this.startWorkoutSessionUseCase = startWorkoutSessionUseCase;
        this.logSetRecordUseCase = logSetRecordUseCase;
    }

    @Operation(summary = "Start a new workout session", description = "Initiates a session to execute a specific routine")
    @ApiResponse(responseCode = "201", description = "Session started successfully")
    @PostMapping
    public ResponseEntity<Void> startSession(@RequestBody StartSessionRequestDto request) {
        Long sessionId = startWorkoutSessionUseCase.execute(request);
        return ResponseEntity.created(URI.create("/api/v1/sessions/" + sessionId)).build();
    }

    @Operation(summary = "Log an exercise set", description = "Logs a single set execution into the active session")
    @ApiResponse(responseCode = "200", description = "Set logged successfully")
    @PostMapping("/{sessionId}/sets")
    public ResponseEntity<SetRecordResponseDto> logSet(
            @PathVariable Long sessionId,
            @RequestBody LogSetRequestDto request) {
        SetRecordResponseDto response = logSetRecordUseCase.execute(sessionId, request);
        return ResponseEntity.ok(response);
    }
}
