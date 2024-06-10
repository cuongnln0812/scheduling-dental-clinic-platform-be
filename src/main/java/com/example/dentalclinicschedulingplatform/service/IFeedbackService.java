package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.SendFeedbackRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.SendFeedbackResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IFeedbackService {
    SendFeedbackResponse sendFeedback(SendFeedbackRequest request);
    List<SendFeedbackResponse> getFeedbackByBranchId(Long branchId);
    List<SendFeedbackResponse> getAllFeedback();
}
