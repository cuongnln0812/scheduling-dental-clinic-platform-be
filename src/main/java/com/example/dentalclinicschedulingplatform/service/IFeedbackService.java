package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.SendFeedbackRequest;
import com.example.dentalclinicschedulingplatform.payload.response.SendFeedbackResponse;
import com.example.dentalclinicschedulingplatform.payload.response.SummaryFeedbackResponse;

public interface IFeedbackService {
    SendFeedbackResponse sendFeedback(SendFeedbackRequest request);
    SummaryFeedbackResponse getFeedbackByBranchId(Long branchId);
    SummaryFeedbackResponse getFeedbackByClinicId(Long clinicId);
    SummaryFeedbackResponse getAllFeedback();
    void deleteFeedback(Long feedbackId);
}
