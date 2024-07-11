package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import com.example.dentalclinicschedulingplatform.entity.Dentist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DentistRepository extends JpaRepository<Dentist, Long> {
    Optional<Dentist> findByUsernameOrEmail(String username, String email);
    Optional<Dentist> findByUsername(String username);
    @Query("SELECT d FROM Dentist d WHERE d.clinicBranch.clinic.clinicId = :clinicId")
    Page<Dentist> findByClinicId(Long clinicId, Pageable pageable);
    Page<Dentist> findAllByClinicBranch_BranchId(Long branchId, Pageable pageRequest);
    Page<Dentist> findAllByStatus(ClinicStatus status, Pageable pageRequest);

    boolean existsByEmailOrUsername(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<Dentist> findAllByClinicBranch_BranchId(Long branchId);
}
