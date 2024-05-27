package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.ClinicOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<ClinicOwner, Long> {
    Optional<ClinicOwner> findByEmail(String email);
    boolean existsByEmail(String email);
}
