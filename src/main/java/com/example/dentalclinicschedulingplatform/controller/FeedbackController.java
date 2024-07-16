package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.DeleteFeedbackRequest;
import com.example.dentalclinicschedulingplatform.payload.request.SendFeedbackRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.SendFeedbackResponse;
import com.example.dentalclinicschedulingplatform.payload.response.SummaryFeedbackResponse;
import com.example.dentalclinicschedulingplatform.service.IFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Feedback Controller")
@RequestMapping("api/v1/feedback")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class FeedbackController {

    private final IFeedbackService feedbackService;

    @Operation(summary = "Create Feedback")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<ApiResponse<SendFeedbackResponse>> createFeedback(@Valid @RequestBody SendFeedbackRequest request) {
        ApiResponse<SendFeedbackResponse> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Send feedback successfully!",
                feedbackService.sendFeedback(request));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get Feedback by Clinic Branch ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'STAFF', 'CUSTOMER', 'DENTIST')")
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<SummaryFeedbackResponse>> getFeedbackByBranchId(@PathVariable Long branchId) {
        SummaryFeedbackResponse feedbackResponses = feedbackService.getFeedbackByBranchId(branchId);
        ApiResponse<SummaryFeedbackResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Feedback by clinic branch",
                feedbackResponses);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get Feedback by Clinic ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'STAFF', 'CUSTOMER', 'DENTIST')")
    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<ApiResponse<SummaryFeedbackResponse>> getFeedbackByClinicId(@PathVariable Long clinicId) {
        SummaryFeedbackResponse feedbackResponses = feedbackService.getFeedbackByClinicId(clinicId);
        ApiResponse<SummaryFeedbackResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Feedback by clinic",
                feedbackResponses);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get All Feedback")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'STAFF', 'CUSTOMER', 'DENTIST')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<SummaryFeedbackResponse>> getAllFeedback() {
        SummaryFeedbackResponse feedbackResponses = feedbackService.getAllFeedback();
        ApiResponse<SummaryFeedbackResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched all feedback successfully!",
                feedbackResponses);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete Feedback")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(@Valid @RequestBody DeleteFeedbackRequest feedbackId) {
        feedbackService.deleteFeedback(feedbackId.getFeedbackID());
        ApiResponse<Void> response = new ApiResponse<>(HttpStatus.OK, "Feedback deleted successfully!", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}