package com.example.dentalclinicschedulingplatform.payload.request;

import com.example.dentalclinicschedulingplatform.entity.ReportReason;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateReportRequest {
    @NotNull
    private Long feedbackId;

    @NotNull
    private List<ReportReason> reportReason;

    @NotNull
    private String reportedCustomer;
}
