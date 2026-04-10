package com.mitra.presentation.controller;

import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.domain.model.User;
import com.mitra.infrastructure.security.TokenService;
import com.mitra.presentation.dto.request.LoginRequestDto;
import com.mitra.presentation.dto.response.TokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Authentication", description = "Endpoints for user authentication")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepositoryPort userRepositoryPort;
    private final TokenService tokenService;

    public AuthController(UserRepositoryPort userRepositoryPort, TokenService tokenService) {
        this.userRepositoryPort = userRepositoryPort;
        this.tokenService = tokenService;
    }

    @Operation(summary = "Login an existing user", description = "For the MVP, receives the email to authenticate and return a JWT")
    @ApiResponse(responseCode = "200", description = "Authentication successful and token returned")
    @ApiResponse(responseCode = "401", description = "Unauthorized - User does not exist")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto request) {
        Optional<User> userOpt = userRepositoryPort.findByEmail(request.email());
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
        User user = userOpt.get();
        String token = tokenService.generateToken(user.getEmail(), user.getId());
        
        return ResponseEntity.ok(new TokenResponseDto(token));
    }
}
