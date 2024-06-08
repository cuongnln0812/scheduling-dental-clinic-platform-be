package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.ClinicStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<ClinicStaff, Long> {
    Optional<ClinicStaff> findByUsernameOrEmail(String username, String email);
    boolean existsByEmailOrUsername(String username, String email);
    @Query(value = "SELECT c.clinic_id " +
            "FROM clinic_staff cs join clinic_branch cb on cs.branch_id = cb.branch_id "+
            "join clinic c on cb.clinic_id = c.clinic_id " +
            "WHERE cs.staff_id = :staffId", nativeQuery = true)
    Long getClinicIdOfStaff(Long staffId);
}
