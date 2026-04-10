package com.mitra.presentation.controller;

import com.mitra.application.usecase.CalculateBmrUseCase;
import com.mitra.presentation.dto.response.BmrResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final CalculateBmrUseCase calculateBmrUseCase;

    public UserController(CalculateBmrUseCase calculateBmrUseCase) {
        this.calculateBmrUseCase = calculateBmrUseCase;
    }

    @GetMapping("/{userId}/bmr")
    public ResponseEntity<BmrResponseDto> getBmr(@PathVariable Long userId) {
        double bmr = calculateBmrUseCase.execute(userId);
        BmrResponseDto response = new BmrResponseDto(bmr, LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
