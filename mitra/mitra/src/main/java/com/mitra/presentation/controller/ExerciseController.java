package com.mitra.presentation.controller;

import com.mitra.application.usecase.CreateExerciseUseCase;
import com.mitra.application.usecase.GetAllExercisesUseCase;
import com.mitra.application.usecase.GetExerciseHistoryUseCase;
import com.mitra.application.usecase.GetPersonalRecordsUseCase;
import com.mitra.domain.model.User;
import com.mitra.presentation.dto.request.CreateExerciseRequestDto;
import com.mitra.presentation.dto.response.ExerciseHistoryResponseDto;
import com.mitra.presentation.dto.response.ExerciseResponseDto;
import com.mitra.presentation.dto.response.PersonalRecordResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Exercises", description = "Endpoints for managing the exercise catalog")
@RestController
@RequestMapping("/api/v1/exercises")
public class ExerciseController {

    private final CreateExerciseUseCase createExerciseUseCase;
    private final GetAllExercisesUseCase getAllExercisesUseCase;
    private final GetExerciseHistoryUseCase getExerciseHistoryUseCase;
    private final GetPersonalRecordsUseCase getPersonalRecordsUseCase;

    public ExerciseController(CreateExerciseUseCase createExerciseUseCase, 
                              GetAllExercisesUseCase getAllExercisesUseCase,
                              GetExerciseHistoryUseCase getExerciseHistoryUseCase,
                              GetPersonalRecordsUseCase getPersonalRecordsUseCase) {
        this.createExerciseUseCase = createExerciseUseCase;
        this.getAllExercisesUseCase = getAllExercisesUseCase;
        this.getExerciseHistoryUseCase = getExerciseHistoryUseCase;
        this.getPersonalRecordsUseCase = getPersonalRecordsUseCase;
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

    @Operation(summary = "Get exercise history", description = "Retrieves the user's history for an exercise grouped by session")
    @ApiResponse(responseCode = "200", description = "History retrieved successfully")
    @GetMapping("/{exerciseId}/history")
    public ResponseEntity<ExerciseHistoryResponseDto> getExerciseHistory(
            @PathVariable Long exerciseId,
            @AuthenticationPrincipal User currentUser) {
        ExerciseHistoryResponseDto response = getExerciseHistoryUseCase.execute(currentUser.getId(), exerciseId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get personal records", description = "Calculates personal records for the user in the specified exercise")
    @ApiResponse(responseCode = "200", description = "PRs retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No sets logged for this exercise")
    @GetMapping("/{exerciseId}/records")
    public ResponseEntity<PersonalRecordResponseDto> getPersonalRecords(
            @PathVariable Long exerciseId,
            @AuthenticationPrincipal User currentUser) {
        PersonalRecordResponseDto response = getPersonalRecordsUseCase.execute(currentUser.getId(), exerciseId);
        return ResponseEntity.ok(response);
    }
}
