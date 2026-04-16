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
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public AuthController(UserRepositoryPort userRepositoryPort, TokenService tokenService,
                          org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Login an existing user", description = "Receives email and password to authenticate and return a JWT")
    @ApiResponse(responseCode = "200", description = "Authentication successful and token returned")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto request) {
        Optional<User> userOpt = userRepositoryPort.findByEmail(request.email());
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
        User user = userOpt.get();
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }
        
        String token = tokenService.generateToken(user.getEmail(), user.getId());
        
        return ResponseEntity.ok(new TokenResponseDto(token));
    }
}
