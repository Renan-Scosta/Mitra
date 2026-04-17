package com.mitra.presentation.controller;

import com.mitra.application.usecase.CreateBodyMeasurementUseCase;
import com.mitra.application.usecase.GetBodyMeasurementsUseCase;
import com.mitra.domain.model.User;
import com.mitra.presentation.dto.request.CreateBodyMeasurementRequestDto;
import com.mitra.presentation.dto.response.BodyMeasurementResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Body Measurements", description = "Endpoints for tracking weight and body composition over time")
@RestController
@RequestMapping("/api/v1/measurements")
public class BodyMeasurementController {

    private final CreateBodyMeasurementUseCase createBodyMeasurementUseCase;
    private final GetBodyMeasurementsUseCase getBodyMeasurementsUseCase;

    public BodyMeasurementController(CreateBodyMeasurementUseCase createBodyMeasurementUseCase,
                                     GetBodyMeasurementsUseCase getBodyMeasurementsUseCase) {
        this.createBodyMeasurementUseCase = createBodyMeasurementUseCase;
        this.getBodyMeasurementsUseCase = getBodyMeasurementsUseCase;
    }

    @Operation(summary = "Record a new measurement", description = "Saves a body measurement for the authenticated user")
    @ApiResponse(responseCode = "201", description = "Measurement recorded successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @PostMapping
    public ResponseEntity<Void> createMeasurement(@Valid @RequestBody CreateBodyMeasurementRequestDto request,
                                                  @AuthenticationPrincipal User currentUser) {
        Long measurementId = createBodyMeasurementUseCase.execute(currentUser.getId(), request);
        return ResponseEntity.created(URI.create("/api/v1/measurements/" + measurementId)).build();
    }

    @Operation(summary = "Get my measurement history", description = "Retrieves all body measurements for the authenticated user, ordered by most recent")
    @ApiResponse(responseCode = "200", description = "Measurements retrieved successfully")
    @GetMapping
    public ResponseEntity<List<BodyMeasurementResponseDto>> getMyMeasurements(@AuthenticationPrincipal User currentUser) {
        List<BodyMeasurementResponseDto> measurements = getBodyMeasurementsUseCase.execute(currentUser.getId());
        return ResponseEntity.ok(measurements);
    }
}
