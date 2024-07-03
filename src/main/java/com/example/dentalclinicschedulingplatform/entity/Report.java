package com.example.dentalclinicschedulingplatform.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "feedback_report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;

    @Column(name = "report_reasons", nullable = false)
    private String reportReasons;

    @CreatedBy
    @Column(name = "reporter", nullable = false, updatable = false)
    private String reporter;

    @CreatedBy
    @Column(name = "reported_customer", nullable = false, updatable = false)
    private String reportedCustomer;

    @Column(name = "created_date_time", updatable = false)
    private LocalDateTime createdDateTime;

    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private ClinicBranch branch;

    public void setReportReasons(List<ReportReason> reasons) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.reportReasons = objectMapper.writeValueAsString(reasons);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting reasons to JSON", e);
        }
    }

    public List<ReportReason> getReportReasons() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(this.reportReasons, objectMapper.getTypeFactory().constructCollectionType(List.class, ReportReason.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to reasons", e);
        }
    }
}
