package com.mitra.presentation.controller;

import com.mitra.application.usecase.LogSetRecordUseCase;
import com.mitra.application.usecase.StartWorkoutSessionUseCase;
import com.mitra.application.usecase.FinishWorkoutSessionUseCase;
import com.mitra.application.usecase.GetWorkoutSessionUseCase;
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
    private final FinishWorkoutSessionUseCase finishWorkoutSessionUseCase;
    private final GetWorkoutSessionUseCase getWorkoutSessionUseCase;

    public WorkoutSessionController(StartWorkoutSessionUseCase startWorkoutSessionUseCase,
                                    LogSetRecordUseCase logSetRecordUseCase,
                                    FinishWorkoutSessionUseCase finishWorkoutSessionUseCase,
                                    GetWorkoutSessionUseCase getWorkoutSessionUseCase) {
        this.startWorkoutSessionUseCase = startWorkoutSessionUseCase;
        this.logSetRecordUseCase = logSetRecordUseCase;
        this.finishWorkoutSessionUseCase = finishWorkoutSessionUseCase;
        this.getWorkoutSessionUseCase = getWorkoutSessionUseCase;
    }

    @Operation(summary = "Start a new workout session", description = "Initiates a session to execute a specific routine for the authenticated user")
    @ApiResponse(responseCode = "201", description = "Session started successfully")
    @PostMapping
    public ResponseEntity<Void> startSession(@RequestBody StartSessionRequestDto request,
                                             @org.springframework.security.core.annotation.AuthenticationPrincipal com.mitra.domain.model.User currentUser) {
        Long sessionId = startWorkoutSessionUseCase.execute(currentUser.getId(), request);
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

    @Operation(summary = "Finish a workout session", description = "Marks the session as finished and calculates summary statistics")
    @ApiResponse(responseCode = "200", description = "Session finished successfully")
    @PostMapping("/{sessionId}/finish")
    public ResponseEntity<com.mitra.presentation.dto.response.SessionSummaryResponseDto> finishSession(@PathVariable Long sessionId) {
        com.mitra.presentation.dto.response.SessionSummaryResponseDto summary = finishWorkoutSessionUseCase.execute(sessionId);
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "Get session details", description = "Retrieves full details of a session, including all logged sets")
    @ApiResponse(responseCode = "200", description = "Session retrieved successfully")
    @GetMapping("/{sessionId}")
    public ResponseEntity<com.mitra.presentation.dto.response.WorkoutSessionResponseDto> getSession(@PathVariable Long sessionId) {
        com.mitra.presentation.dto.response.WorkoutSessionResponseDto sessionDto = getWorkoutSessionUseCase.execute(sessionId);
        return ResponseEntity.ok(sessionDto);
    }
}
