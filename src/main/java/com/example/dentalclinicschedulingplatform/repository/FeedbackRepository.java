package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByClinicBranch_BranchId(Long branchId);
}
