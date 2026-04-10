package com.mitra.presentation.controller;

import com.mitra.application.usecase.CalculateBmrUseCase;
import com.mitra.application.usecase.RegisterUserUseCase;
import com.mitra.presentation.dto.request.CreateUserRequestDto;
import com.mitra.presentation.dto.response.BmrResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final CalculateBmrUseCase calculateBmrUseCase;
    private final RegisterUserUseCase registerUserUseCase;

    public UserController(CalculateBmrUseCase calculateBmrUseCase, RegisterUserUseCase registerUserUseCase) {
        this.calculateBmrUseCase = calculateBmrUseCase;
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> registerUser(@RequestBody CreateUserRequestDto request) {
        Long userId = registerUserUseCase.execute(request);
        return ResponseEntity.created(URI.create("/api/v1/users/" + userId)).build();
    }

    @GetMapping("/{userId}/bmr")
    public ResponseEntity<BmrResponseDto> getBmr(@PathVariable Long userId) {
        double bmr = calculateBmrUseCase.execute(userId);
        BmrResponseDto response = new BmrResponseDto(bmr, LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}

