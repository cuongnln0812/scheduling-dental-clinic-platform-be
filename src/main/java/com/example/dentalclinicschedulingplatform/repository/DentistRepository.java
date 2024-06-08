package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.Dentist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DentistRepository extends JpaRepository<Dentist, Long> {
    Optional<Dentist> findByUsernameOrEmail(String username, String email);
    Page<Dentist> findAllByClinicBranch_Id(Long branchId, Pageable pageRequest);
    Page<Dentist> findAll(Pageable pageable);

    boolean existsByEmailOrUsername(String username, String email);
}
