package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query(value = "SELECT * FROM appointment where appointment_date >= :startDate and appointment_date <= :endDate " +
            "and branch_id = :clinicBranchId ", nativeQuery = true)
    List<Appointment> findByStartDateAndEndDateOfClinicBranch(LocalDate startDate, LocalDate endDate, Long clinicBranchId);

    @Query(value = "SELECT * FROM appointment where appointment_date=:date " +
            "and branch_id = :clinicBranchId ", nativeQuery = true)
    List<Appointment> findByDateOfClinicBranch(LocalDate date, Long clinicBranchId);
    Page<Appointment> findAppointmentsByCustomerId(Long customerId, Pageable pageable);
    Page<Appointment> findAppointmentsByClinicBranch_BranchId(Long branchId, Pageable pageable);
    Page<Appointment> findAppointmentsByDentistId(Long dentistId, Pageable pageable);
}
