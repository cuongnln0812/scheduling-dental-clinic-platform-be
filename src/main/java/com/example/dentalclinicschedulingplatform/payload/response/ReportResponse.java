package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.ReportReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    private Long reportId;
    private Long feedbackId;
    private String reportReasons;
    private String reporter;
    private String reportedCustomer;
    private LocalDateTime createdDateTime;
    private Long clinicId;
    private Long branchId;
    private String avatar;
    private String fullName;
    private String branchName;
    private String city;
    private String comment;
    private Double rating;
    private Double averageRating;
    private Long totalFeedback;

}
