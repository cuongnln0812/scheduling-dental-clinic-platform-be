package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.ClinicOwner;
import com.example.dentalclinicschedulingplatform.entity.Dentist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<ClinicOwner, Long> {
    Optional<ClinicOwner> findByUsernameOrEmail(String username, String email);
    Optional<ClinicOwner> findByUsername(String username);
    boolean existsByEmailOrUsername(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    @Query("SELECT d FROM ClinicOwner d WHERE d.status = 'INACTIVE' OR d.status = 'ACTIVE'")
    Page<ClinicOwner> findAllActiveAndInactive(Pageable pageable);
;}
