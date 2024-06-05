package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;
import com.example.dentalclinicschedulingplatform.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClinicBranchRepository extends JpaRepository<ClinicBranch, Long> {

    @Override
    Optional<ClinicBranch> findById(Long id);

    @Query(value = "SELECT cb.branch_id FROM public.clinic_branch cb " +
                   "where cb.clinic_id = " +
                   "(SELECT clinic_id FROM public.clinic c WHERE c.owner_id = :ownerId) " +
                   "AND cb.branch_id = :branchId;" , nativeQuery = true)
    Optional<ClinicBranch> findAllBelongClinicOwner(@Param("branchId") Long branchId,
                                                    @Param("ownerId") Long ownerId);

}
