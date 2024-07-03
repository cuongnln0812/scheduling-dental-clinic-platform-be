package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    @Override
    Optional<Report> findById(Long reportId);

    List<Report> findByClinic_ClinicId(Long clinicId);

    List<Report> findByBranch_BranchId(Long branchId);

    Page<Report> findByClinic_ClinicId(Long clinicId, Pageable pageable);

    Page<Report> findAll(Pageable pageable);

    Optional<Report> findByFeedback_FeedbackIdAndReporter(Long feedbackId, String reporter);
}
