package com.mitra.presentation.controller;

import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.application.usecase.GoogleLoginUseCase;
import com.mitra.domain.model.User;
import com.mitra.infrastructure.security.TokenService;
import com.mitra.presentation.dto.request.GoogleLoginRequestDto;
import com.mitra.presentation.dto.request.LoginRequestDto;
import com.mitra.presentation.dto.response.TokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Authentication", description = "Endpoints for user authentication")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepositoryPort userRepositoryPort;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final GoogleLoginUseCase googleLoginUseCase;

    public AuthController(UserRepositoryPort userRepositoryPort, TokenService tokenService,
                          PasswordEncoder passwordEncoder, GoogleLoginUseCase googleLoginUseCase) {
        this.userRepositoryPort = userRepositoryPort;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.googleLoginUseCase = googleLoginUseCase;
    }

    @Operation(summary = "Login an existing user", description = "Receives email and password to authenticate and return a JWT")
    @ApiResponse(responseCode = "200", description = "Authentication successful and token returned")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
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

    @Operation(summary = "Login with Google", description = "Receives a Google idToken and returns a JWT")
    @ApiResponse(responseCode = "200", description = "Authentication successful and token returned")
    @ApiResponse(responseCode = "400", description = "Invalid token")
    @PostMapping("/google")
    public ResponseEntity<TokenResponseDto> googleLogin(@Valid @RequestBody GoogleLoginRequestDto request) {
        try {
            String token = googleLoginUseCase.execute(request.idToken());
            return ResponseEntity.ok(new TokenResponseDto(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
