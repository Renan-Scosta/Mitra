package com.mitra.presentation.controller;

import com.mitra.application.usecase.CalculateBmrUseCase;
import com.mitra.application.usecase.RegisterUserUseCase;
import com.mitra.presentation.dto.request.CreateUserRequestDto;
import com.mitra.presentation.dto.response.BmrResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@Tag(name = "Users", description = "Endpoints for user management and baseline body metrics")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final CalculateBmrUseCase calculateBmrUseCase;
    private final RegisterUserUseCase registerUserUseCase;

    public UserController(CalculateBmrUseCase calculateBmrUseCase, RegisterUserUseCase registerUserUseCase) {
        this.calculateBmrUseCase = calculateBmrUseCase;
        this.registerUserUseCase = registerUserUseCase;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user in the database with the initial information")
    @ApiResponse(responseCode = "201", description = "User successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @PostMapping
    public ResponseEntity<Void> registerUser(@RequestBody CreateUserRequestDto request) {
        Long userId = registerUserUseCase.execute(request);
        return ResponseEntity.created(URI.create("/api/v1/users/" + userId)).build();
    }

    @Operation(summary = "Calculate BMR", description = "Calculates the current Basal Metabolic Rate based on the Harris-Benedict formula using the last recorded weight")
    @ApiResponse(responseCode = "200", description = "Calculation completed successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{userId}/bmr")
    public ResponseEntity<BmrResponseDto> getBmr(@PathVariable Long userId) {
        double bmr = calculateBmrUseCase.execute(userId);
        BmrResponseDto response = new BmrResponseDto(bmr, LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}

