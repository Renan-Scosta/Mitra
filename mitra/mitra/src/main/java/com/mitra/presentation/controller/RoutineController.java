package com.mitra.presentation.controller;

import com.mitra.application.usecase.AddRoutineExerciseUseCase;
import com.mitra.application.usecase.CreateWorkoutRoutineUseCase;
import com.mitra.application.usecase.GetWorkoutRoutinesUseCase;
import com.mitra.domain.model.User;
import com.mitra.presentation.dto.request.AddRoutineExerciseRequestDto;
import com.mitra.presentation.dto.request.CreateRoutineRequestDto;
import com.mitra.presentation.dto.response.RoutineExerciseResponseDto;
import com.mitra.presentation.dto.response.RoutineResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Workout Routines", description = "Endpoints for managing workout routines and adding exercises")
@RestController
@RequestMapping("/api/v1/routines")
public class RoutineController {

    private final CreateWorkoutRoutineUseCase createWorkoutRoutineUseCase;
    private final GetWorkoutRoutinesUseCase getWorkoutRoutinesUseCase;
    private final AddRoutineExerciseUseCase addRoutineExerciseUseCase;

    public RoutineController(
            CreateWorkoutRoutineUseCase createWorkoutRoutineUseCase,
            GetWorkoutRoutinesUseCase getWorkoutRoutinesUseCase,
            AddRoutineExerciseUseCase addRoutineExerciseUseCase) {
        this.createWorkoutRoutineUseCase = createWorkoutRoutineUseCase;
        this.getWorkoutRoutinesUseCase = getWorkoutRoutinesUseCase;
        this.addRoutineExerciseUseCase = addRoutineExerciseUseCase;
    }

    @Operation(summary = "Create a new routine", description = "Creates a new empty workout routine for the authenticated user")
    @ApiResponse(responseCode = "201", description = "Routine successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @PostMapping
    public ResponseEntity<Void> createRoutine(@Valid @RequestBody CreateRoutineRequestDto request,
                                              @AuthenticationPrincipal User currentUser) {
        Long routineId = createWorkoutRoutineUseCase.execute(currentUser.getId(), request);
        return ResponseEntity.created(URI.create("/api/v1/routines/" + routineId)).build();
    }

    @Operation(summary = "Get my routines", description = "Retrieves all workout routines belonging to the authenticated user")
    @ApiResponse(responseCode = "200", description = "Routines retrieved successfully")
    @GetMapping
    public ResponseEntity<List<RoutineResponseDto>> getMyRoutines(@AuthenticationPrincipal User currentUser) {
        List<RoutineResponseDto> routines = getWorkoutRoutinesUseCase.execute(currentUser.getId());
        return ResponseEntity.ok(routines);
    }

    @Operation(summary = "Add an exercise to a routine", description = "Maps an exercise to a given routine with target sets and reps")
    @ApiResponse(responseCode = "200", description = "Exercise added to routine successfully")
    @ApiResponse(responseCode = "403", description = "Routine does not belong to the authenticated user")
    @ApiResponse(responseCode = "404", description = "Routine or Exercise not found")
    @PostMapping("/{routineId}/exercises")
    public ResponseEntity<RoutineExerciseResponseDto> addExerciseToRoutine(
            @PathVariable Long routineId,
            @Valid @RequestBody AddRoutineExerciseRequestDto request,
            @AuthenticationPrincipal User currentUser) {
        RoutineExerciseResponseDto response = addRoutineExerciseUseCase.execute(currentUser.getId(), routineId, request);
        return ResponseEntity.ok(response);
    }
}
