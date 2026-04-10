package com.mitra.presentation.controller;

import com.mitra.application.usecase.AddRoutineExerciseUseCase;
import com.mitra.application.usecase.CreateWorkoutRoutineUseCase;
import com.mitra.application.usecase.GetWorkoutRoutinesUseCase;
import com.mitra.presentation.dto.request.AddRoutineExerciseRequestDto;
import com.mitra.presentation.dto.request.CreateRoutineRequestDto;
import com.mitra.presentation.dto.response.RoutineExerciseResponseDto;
import com.mitra.presentation.dto.response.RoutineResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Create a new routine", description = "Creates a new empty workout routine for a user")
    @ApiResponse(responseCode = "201", description = "Routine successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @PostMapping
    public ResponseEntity<Void> createRoutine(@RequestBody CreateRoutineRequestDto request) {
        Long routineId = createWorkoutRoutineUseCase.execute(request);
        return ResponseEntity.created(URI.create("/api/v1/routines/" + routineId)).build();
    }

    @Operation(summary = "Get user's routines", description = "Retrieves all workout routines belonging to a specific user")
    @ApiResponse(responseCode = "200", description = "Routines retrieved successfully")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoutineResponseDto>> getUserRoutines(@PathVariable Long userId) {
        List<RoutineResponseDto> routines = getWorkoutRoutinesUseCase.execute(userId);
        return ResponseEntity.ok(routines);
    }

    @Operation(summary = "Add an exercise to a routine", description = "Maps an exercise to a given routine with target sets and reps")
    @ApiResponse(responseCode = "200", description = "Exercise added to routine successfully")
    @ApiResponse(responseCode = "404", description = "Routine or Exercise not found")
    @PostMapping("/{routineId}/exercises")
    public ResponseEntity<RoutineExerciseResponseDto> addExerciseToRoutine(
            @PathVariable Long routineId,
            @RequestBody AddRoutineExerciseRequestDto request) {
        RoutineExerciseResponseDto response = addRoutineExerciseUseCase.execute(routineId, request);
        return ResponseEntity.ok(response);
    }
}
