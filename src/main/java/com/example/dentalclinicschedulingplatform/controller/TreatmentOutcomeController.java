package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.TreatmentOutcomeRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.TreatmentOutcomeResponse;
import com.example.dentalclinicschedulingplatform.service.ITreatmentOutcomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/treatment-outcomes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Treatment Outcomes Controller")
public class TreatmentOutcomeController {

    private final ITreatmentOutcomeService treatmentOutcomeService;

    @Operation(summary = "Create Treatment Outcome")
    @PostMapping
    public ResponseEntity<ApiResponse<TreatmentOutcomeResponse>> createTreatmentOutcome(@RequestBody TreatmentOutcomeRequest request) {
        TreatmentOutcomeResponse response = treatmentOutcomeService.createTreatmentOutcome(request);
        ApiResponse<TreatmentOutcomeResponse> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                "Treatment outcome created successfully!",
                response);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Update Treatment Outcome")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TreatmentOutcomeResponse>> updateTreatmentOutcome(@PathVariable Long id, @RequestBody TreatmentOutcomeRequest request) {
        TreatmentOutcomeResponse response = treatmentOutcomeService.updateTreatmentOutcome(id, request);
        ApiResponse<TreatmentOutcomeResponse> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Treatment outcome updated successfully!",
                response);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Remove Treatment Outcome")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeTreatmentOutcome(@PathVariable Long id) {
        treatmentOutcomeService.removeTreatmentOutcome(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Treatment outcome removed successfully!",
                null);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "View Treatment Outcomes by Customer")
    @GetMapping("/customer")
    public ResponseEntity<ApiResponse<List<TreatmentOutcomeResponse>>> viewTreatmentOutcomesByCustomer(@RequestParam String username) {
        List<TreatmentOutcomeResponse> responses = treatmentOutcomeService.viewTreatmentOutcomesByCustomer(username);
        ApiResponse<List<TreatmentOutcomeResponse>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Treatment outcomes by customer",
                responses);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "View Treatment Outcome by Appointment")
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<TreatmentOutcomeResponse>> viewTreatmentOutcomeByAppointment(@PathVariable Long appointmentId) {
        TreatmentOutcomeResponse response = treatmentOutcomeService.viewTreatmentOutcomeByAppointment(appointmentId);
        ApiResponse<TreatmentOutcomeResponse> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Treatment outcome by appointment",
                response);
        return ResponseEntity.ok(apiResponse);
    }
}