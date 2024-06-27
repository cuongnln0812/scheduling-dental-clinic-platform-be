package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.CreateReportRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.ReportResponse;
import com.example.dentalclinicschedulingplatform.service.IReportFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Report Feedback Controller")
@RequestMapping("api/v1/report")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ReportFeedbackController {

    private final IReportFeedbackService reportFeedbackService;

    @Operation(summary = "Create Report")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ReportResponse>> createReport(@Valid @RequestBody CreateReportRequest request) {
        ApiResponse<ReportResponse> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Report created successfully!",
                reportFeedbackService.createReport(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get All Reports")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getAllReports() {
        ApiResponse<List<ReportResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched all reports successfully!",
                reportFeedbackService.getAllReports());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get Reports by Clinic ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'STAFF')")
    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getReportsByClinicId(@PathVariable Long clinicId) {
        ApiResponse<List<ReportResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched reports by clinic ID successfully!",
                reportFeedbackService.getReportsByClinicId(clinicId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Approve Report")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/approve/{reportId}")
    public ResponseEntity<ApiResponse<Void>> approveReport(@PathVariable Long reportId) {
        reportFeedbackService.approveReport(reportId);
        ApiResponse<Void> response = new ApiResponse<>(HttpStatus.OK, "Report approved successfully!", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Decline Report")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/decline/{reportId}")
    public ResponseEntity<ApiResponse<Void>> declineReport(@PathVariable Long reportId) {
        reportFeedbackService.declineReport(reportId);
        ApiResponse<Void> response = new ApiResponse<>(HttpStatus.OK, "Report declined successfully!", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}