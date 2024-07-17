package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.Appointment;
import com.example.dentalclinicschedulingplatform.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query(value = "SELECT * FROM appointment where appointment_date >= :startDate and appointment_date <= :endDate " +
            "and branch_id = :clinicBranchId and status = 'PENDING' ", nativeQuery = true)
    List<Appointment> findByStartDateAndEndDateOfClinicBranch(LocalDate startDate, LocalDate endDate, Long clinicBranchId);

    @Query(value = "SELECT * FROM appointment where appointment_date=:date " +
            "and branch_id = :clinicBranchId and status = 'PENDING' ", nativeQuery = true)
    List<Appointment> findByDateOfClinicBranch(LocalDate date, Long clinicBranchId);
    Page<Appointment> findAppointmentsByCustomerId(Long customerId, Pageable pageable);
    Page<Appointment> findAppointmentsByClinicBranch_BranchId(Long branchId, Pageable pageable);

    Page<Appointment> findAppointmentsByDentistId(Long dentistId, Pageable pageable);

    @Query(value = "SELECT * FROM appointment where appointment_date=:date " +
            "and branch_id = :clinicBranchId and slot_id = :slotId and status = 'PENDING' ", nativeQuery = true)
    List<Appointment> findByDateAndSlotOfClinicBranch(LocalDate date, Long clinicBranchId, Long slotId);

    @Query(value = "SELECT * FROM appointment where branch_id in (:branchList) and slot_id in (:slotList) and status = 'PENDING' ", nativeQuery = true)
    List<Appointment> findByClinicBranch(List<Long> branchList, List<Long> slotList);

    List<Appointment> findAllByReminderSentIsFalseAndAppointmentDateAndStatus(LocalDate date, AppointmentStatus status);

    @Query(value = "SELECT a FROM Appointment a WHERE a.clinicBranch.clinic.clinicOwner.username = :username")
    List<Appointment> findAllByOwnerUsername(String username);
}
