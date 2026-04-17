package com.mitra.presentation.controller;

import com.mitra.application.usecase.CreateExerciseUseCase;
import com.mitra.application.usecase.GetAllExercisesUseCase;
import com.mitra.presentation.dto.request.CreateExerciseRequestDto;
import com.mitra.presentation.dto.response.ExerciseResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Exercises", description = "Endpoints for managing the exercise catalog")
@RestController
@RequestMapping("/api/v1/exercises")
public class ExerciseController {

    private final CreateExerciseUseCase createExerciseUseCase;
    private final GetAllExercisesUseCase getAllExercisesUseCase;

    public ExerciseController(CreateExerciseUseCase createExerciseUseCase, GetAllExercisesUseCase getAllExercisesUseCase) {
        this.createExerciseUseCase = createExerciseUseCase;
        this.getAllExercisesUseCase = getAllExercisesUseCase;
    }

    @Operation(summary = "Register a new exercise", description = "Creates a new exercise in the catalog")
    @ApiResponse(responseCode = "201", description = "Exercise successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @PostMapping
    public ResponseEntity<Void> createExercise(@Valid @RequestBody CreateExerciseRequestDto request) {
        Long exerciseId = createExerciseUseCase.execute(request);
        return ResponseEntity.created(URI.create("/api/v1/exercises/" + exerciseId)).build();
    }

    @Operation(summary = "Get all exercises", description = "Retrieves the full catalog of available exercises")
    @ApiResponse(responseCode = "200", description = "Catalog retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ExerciseResponseDto>> getAllExercises() {
        List<ExerciseResponseDto> exercises = getAllExercisesUseCase.execute();
        return ResponseEntity.ok(exercises);
    }
}
