package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.ReportReason;

import java.time.LocalDateTime;

public class ReportResponse {
    private Long reportId;
    private Long feedbackId;
    private ReportReason reportReason;
    private String reporter;
    private String reportedCustomer;
    private LocalDateTime createdDateTime;
    private Long clinicId;
    private Long branchId;
}
