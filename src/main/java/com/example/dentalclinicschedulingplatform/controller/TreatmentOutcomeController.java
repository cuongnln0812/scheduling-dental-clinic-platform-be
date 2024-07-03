package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.TreatmentOutcomeRequest;
import com.example.dentalclinicschedulingplatform.payload.response.TreatmentOutcomeResponse;
import com.example.dentalclinicschedulingplatform.service.ITreatmentOutcomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<TreatmentOutcomeResponse> createTreatmentOutcome(@RequestBody TreatmentOutcomeRequest request) {
        TreatmentOutcomeResponse response = treatmentOutcomeService.createTreatmentOutcome(request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Update Treatment Outcome")
    @PutMapping("/{id}")
    public ResponseEntity<TreatmentOutcomeResponse> updateTreatmentOutcome(@PathVariable Long id, @RequestBody TreatmentOutcomeRequest request) {
        TreatmentOutcomeResponse response = treatmentOutcomeService.updateTreatmentOutcome(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove Treatment Outcome")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeTreatmentOutcome(@PathVariable Long id) {
        treatmentOutcomeService.removeTreatmentOutcome(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "View Treatment Outcomes by Customer")
    @GetMapping("/customer")
    public ResponseEntity<List<TreatmentOutcomeResponse>> viewTreatmentOutcomesByCustomer(@RequestParam String username) {
        List<TreatmentOutcomeResponse> responses = treatmentOutcomeService.viewTreatmentOutcomesByCustomer(username);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "View Treatment Outcome by Appointment")
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<TreatmentOutcomeResponse> viewTreatmentOutcomeByAppointment(@PathVariable Long appointmentId) {
        TreatmentOutcomeResponse response = treatmentOutcomeService.viewTreatmentOutcomeByAppointment(appointmentId);
        return ResponseEntity.ok(response);
    }
}
