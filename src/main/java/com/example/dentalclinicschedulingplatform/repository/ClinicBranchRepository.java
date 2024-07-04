package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;
import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import org.hibernate.engine.spi.Status;
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

}
