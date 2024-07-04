package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import com.example.dentalclinicschedulingplatform.entity.Dentist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DentistRepository extends JpaRepository<Dentist, Long> {
    Optional<Dentist> findByUsernameOrEmail(String username, String email);
    Optional<Dentist> findByUsername(String username);
    Page<Dentist> findAllByClinicBranch_BranchId(Long branchId, Pageable pageRequest);
    Page<Dentist> findAllByStatus(ClinicStatus status, Pageable pageRequest);

    boolean existsByEmailOrUsername(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<Dentist> findAllByClinicBranch_BranchId(Long branchId);
}
