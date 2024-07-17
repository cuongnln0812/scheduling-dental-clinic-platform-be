package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByClinicBranch_BranchId(Long branchId);
    List<Feedback> findByClinicBranch_Clinic_ClinicId(Long clinicId);

    @Query(value = "SELECT COUNT(f) FROM Feedback f WHERE f.clinicBranch.clinic.clinicOwner.username = :username")
    Long countAllByOwnerUsername(String username);
}
