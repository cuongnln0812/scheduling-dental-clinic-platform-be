package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.CreateReportRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ReportResponse;

import java.util.List;

public interface IReportFeedbackService {
    ReportResponse createReport(CreateReportRequest request);
    List<ReportResponse> getAllReports();
    List<ReportResponse> getReportsByClinicId(Long clinicId);
    void approveReport(Long reportId);
    void declineReport(Long reportId);
}
