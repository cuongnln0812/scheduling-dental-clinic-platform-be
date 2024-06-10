package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.SendFeedbackRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.SendFeedbackResponse;
import org.springframework.http.ResponseEntity;

public interface IFeedbackService {
    SendFeedbackResponse sendFeedback(SendFeedbackRequest request);
}
