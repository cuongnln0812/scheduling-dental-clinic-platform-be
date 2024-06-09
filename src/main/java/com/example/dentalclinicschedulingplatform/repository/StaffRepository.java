package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;
import com.example.dentalclinicschedulingplatform.entity.ClinicStaff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<ClinicStaff, Long> {
    Optional<ClinicStaff> findByUsernameOrEmail(String username, String email);
    Optional<ClinicStaff> findById(Long id);
    boolean existsByEmailOrUsername(String username, String email);
    boolean existsByUsername(String username);


    @Query(value = "SELECT s.* " +
                   "FROM clinic_staff s " +
                   "JOIN clinic_branch sc ON s.branch_id = sc.branch_id " +
                   "JOIN clinic c ON sc.clinic_id = c.clinic_id " +
                   "JOIN clinic_owner co ON c.owner_id = co.owner_id " +
                   "WHERE co.owner_id = :ownerId", nativeQuery = true)
    List<ClinicStaff> findAllStaffByOwnerId(@Param("ownerId") Long ownerId);

    @Query(value = "SELECT s.* " +
                   "FROM clinic_staff s " +
                   "JOIN clinic_branch sc ON s.branch_id = sc.branch_id " +
                   "JOIN clinic c ON sc.clinic_id = c.clinic_id " +
                   "JOIN clinic_owner co ON c.owner_id = co.owner_id " +
                   "WHERE co.owner_id = :ownerId", nativeQuery = true)
    Page<ClinicStaff> findAllStaffByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    Page<ClinicStaff> findAllByClinicBranch_Id(Long branchId, Pageable pageable);
    Page<ClinicStaff> findAll(Pageable pageable);
}
