package com.example.dentalclinicschedulingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    private ReportReason reportReason;

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
}
