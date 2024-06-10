package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.SendFeedbackRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CustomerRegisterResponse;
import com.example.dentalclinicschedulingplatform.payload.response.SendFeedbackResponse;
import com.example.dentalclinicschedulingplatform.service.IFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Feedback Controller")
@RequestMapping("api/v1/feedback")
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
}
