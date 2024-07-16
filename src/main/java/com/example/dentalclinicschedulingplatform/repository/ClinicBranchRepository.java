package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;
import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClinicBranchRepository extends JpaRepository<ClinicBranch, Long> {

    @Override
    Optional<ClinicBranch> findById(Long id);

    List<ClinicBranch> findAllByClinic_ClinicId(Long id);

    @Query("SELECT b FROM ClinicBranch b WHERE b.clinic.clinicId = :id AND b.isMain = true")
    Optional<ClinicBranch> findByClinic_ClinicIdAndIsMain(Long id);

    List<ClinicBranch> findAllByClinic_ClinicIdAndStatus(Long id, ClinicStatus status);

    @Query(value = "SELECT cb.* FROM public.clinic_branch cb " +
                   "WHERE cb.clinic_id = " +
                   "(SELECT c.clinic_id FROM public.clinic c WHERE c.owner_id = :ownerId) " +
                   "AND cb.branch_id = :branchId", nativeQuery = true)
    Optional<ClinicBranch> findByBranchIdAndOwnerId(@Param("branchId") Long branchId,
                                                    @Param("ownerId") Long ownerId);
    ClinicBranch findByPhone(String phone);
    ClinicBranch findByAddress(String address);

    Page<ClinicBranch> findAllByStatusAndIsMain(ClinicStatus status, boolean isMain, Pageable pageable);

    @Query(value = "SELECT branch_id FROM clinic_branch where clinic_id = :clinicId and status = 'ACTIVE' ", nativeQuery = true)
    List<Long> findClinicBranchIdsByClinic(Long clinicId);
}
