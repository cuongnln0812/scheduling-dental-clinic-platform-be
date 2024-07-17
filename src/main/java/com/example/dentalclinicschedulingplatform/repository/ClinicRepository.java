package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.Clinic;
import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    Optional<Clinic> findById(Long id);
    Optional<Clinic> findByClinicOwnerId(Long clinicOwnerId);
    Page<Clinic> findAllByStatus(ClinicStatus status, Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsByWebsiteUrl(String url);
    boolean existsByPhone(String phone);

    @Query("SELECT c FROM Clinic c WHERE LOWER(c.clinicName) LIKE LOWER(CONCAT('%', :searchValue, '%'))")

    Page<Clinic> findClinic(String searchValue, Pageable pageable);

}
