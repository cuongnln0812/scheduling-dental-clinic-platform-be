package com.example.dentalclinicschedulingplatform.payload.request;

import com.example.dentalclinicschedulingplatform.entity.ReportReason;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReportRequest {
    @NotNull
    private Long feedbackId;

    @NotNull
    private ReportReason reportReason;

    @NotNull
    private String reporter;

    @NotNull
    private String reportedCustomer;
}
