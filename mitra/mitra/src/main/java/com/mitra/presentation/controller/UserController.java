package com.mitra.presentation.controller;

import com.mitra.application.usecase.CalculateBmrUseCase;
import com.mitra.application.usecase.RegisterUserUseCase;
import com.mitra.application.usecase.UpdateUserProfileUseCase;
import com.mitra.application.usecase.UpdateUserPasswordUseCase;
import com.mitra.application.usecase.DeleteUserAccountUseCase;
import com.mitra.application.usecase.GetUserDashboardUseCase;
import com.mitra.application.usecase.GetUserVolumeSummaryUseCase;
import com.mitra.application.usecase.GetBodyMeasurementsUseCase;
import com.mitra.domain.model.User;
import com.mitra.presentation.dto.request.CreateUserRequestDto;
import com.mitra.presentation.dto.response.BmrResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@Tag(name = "Users", description = "Endpoints for user management and baseline body metrics")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final CalculateBmrUseCase calculateBmrUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final UpdateUserPasswordUseCase updateUserPasswordUseCase;
    private final DeleteUserAccountUseCase deleteUserAccountUseCase;
    private final GetUserDashboardUseCase getUserDashboardUseCase;
    private final GetUserVolumeSummaryUseCase getUserVolumeSummaryUseCase;

    public UserController(CalculateBmrUseCase calculateBmrUseCase,
                          RegisterUserUseCase registerUserUseCase,
                          UpdateUserProfileUseCase updateUserProfileUseCase,
                          UpdateUserPasswordUseCase updateUserPasswordUseCase,
                          DeleteUserAccountUseCase deleteUserAccountUseCase,
                          GetUserDashboardUseCase getUserDashboardUseCase,
                          GetUserVolumeSummaryUseCase getUserVolumeSummaryUseCase) {
        this.calculateBmrUseCase = calculateBmrUseCase;
        this.registerUserUseCase = registerUserUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.updateUserPasswordUseCase = updateUserPasswordUseCase;
        this.deleteUserAccountUseCase = deleteUserAccountUseCase;
        this.getUserDashboardUseCase = getUserDashboardUseCase;
        this.getUserVolumeSummaryUseCase = getUserVolumeSummaryUseCase;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user in the database with the initial information")
    @ApiResponse(responseCode = "201", description = "User successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @PostMapping
    public ResponseEntity<Object> registerUser(@Valid @RequestBody CreateUserRequestDto request) {
        if (!request.password().equals(request.confirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        Long userId = registerUserUseCase.execute(request);
        return ResponseEntity.created(URI.create("/api/v1/users/" + userId)).build();
    }

    @Operation(summary = "Calculate my BMR", description = "Calculates the current Basal Metabolic Rate based on the Mifflin-St Jeor formula using the last recorded weight for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Calculation completed successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/me/bmr")
    public ResponseEntity<BmrResponseDto> getMyBmr(@AuthenticationPrincipal User currentUser) {
        double bmr = calculateBmrUseCase.execute(currentUser.getId());
        BmrResponseDto response = new BmrResponseDto(bmr, LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user profile", description = "Updates basic user profile information like name, birth date, gender, and height")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @PutMapping("/me")
    public ResponseEntity<com.mitra.presentation.dto.response.UserProfileResponseDto> updateProfile(
            @Valid @RequestBody com.mitra.presentation.dto.request.UpdateUserProfileRequestDto request,
            @AuthenticationPrincipal User currentUser) {
        var response = updateUserProfileUseCase.execute(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Change password", description = "Changes the user password. Requires the current password for security verification.")
    @ApiResponse(responseCode = "204", description = "Password changed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data or passwords do not match")
    @ApiResponse(responseCode = "403", description = "Current password is incorrect")
    @PutMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
            @Valid @RequestBody com.mitra.presentation.dto.request.UpdateUserPasswordRequestDto request,
            @AuthenticationPrincipal User currentUser) {
        updateUserPasswordUseCase.execute(currentUser.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete my account", description = "Permanently deletes the authenticated user's account and all associated data.")
    @ApiResponse(responseCode = "204", description = "Account deleted successfully")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal User currentUser) {
        deleteUserAccountUseCase.execute(currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get User Dashboard", description = "Retrieves high-level summary statistics including current streak, workouts this week, calories, and latest session data.")
    @ApiResponse(responseCode = "200", description = "Dashboard retrieved successfully")
    @GetMapping("/me/dashboard")
    public ResponseEntity<com.mitra.presentation.dto.response.DashboardResponseDto> getDashboard(@AuthenticationPrincipal User currentUser) {
        var response = getUserDashboardUseCase.execute(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Volume Summary", description = "Retrieves the total lifted volume per muscle group across a specified date range.")
    @ApiResponse(responseCode = "200", description = "Volume summary retrieved successfully")
    @GetMapping("/me/volume-summary")
    public ResponseEntity<java.util.List<com.mitra.presentation.dto.response.VolumeSummaryResponseDto>> getVolumeSummary(
            @AuthenticationPrincipal User currentUser,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(7).toString()}") String startDate,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "#{T(java.time.LocalDate).now().toString()}") String endDate) {
        
        java.time.LocalDateTime start = java.time.LocalDate.parse(startDate).atStartOfDay();
        java.time.LocalDateTime end = java.time.LocalDate.parse(endDate).atTime(23, 59, 59);
        
        var response = getUserVolumeSummaryUseCase.execute(currentUser.getId(), start, end);
        return ResponseEntity.ok(response);
    }
}
