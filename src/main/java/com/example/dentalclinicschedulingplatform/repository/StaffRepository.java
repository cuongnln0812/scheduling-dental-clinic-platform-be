package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.ClinicStaff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<ClinicStaff, Long> {
    Optional<ClinicStaff> findByUsernameOrEmail(String username, String email);
    boolean existsByEmailOrUsername(String username, String email);
}
