package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.SendFeedbackRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.SendFeedbackResponse;
import com.example.dentalclinicschedulingplatform.service.IFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Feedback Controller")
@RequestMapping("api/v1/feedback")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class FeedbackController {

    private final IFeedbackService feedbackService;

    @Operation(
            summary = "Create Feedback"
    )
    @PostMapping()
    public ResponseEntity<ApiResponse<SendFeedbackResponse>> createFeedback(@Valid @RequestBody SendFeedbackRequest request) {
        ApiResponse<SendFeedbackResponse> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Send feedback successfully!",
                feedbackService.sendFeedback (request));
        return new ResponseEntity<> (response, HttpStatus.OK);
    }

    @Operation(summary = "Get Feedback by Clinic Branch ID")
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<SendFeedbackResponse>>> getFeedbackByBranchId(@PathVariable Long branchId) {
        List<SendFeedbackResponse> feedbackResponses = feedbackService.getFeedbackByBranchId(branchId);
        ApiResponse<List<SendFeedbackResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Feedback by clinic branch",
                feedbackResponses);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get All Feedback")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<SendFeedbackResponse>>> getAllFeedback() {
        List<SendFeedbackResponse> feedbackResponses = feedbackService.getAllFeedback();
        ApiResponse<List<SendFeedbackResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched all feedback successfully!",
                feedbackResponses);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
