package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.TreatmentOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentOutcomeRepository extends JpaRepository<TreatmentOutcome, Long> {
    List<TreatmentOutcome> findByCustomerUsername(String username);
    Optional<TreatmentOutcome> findByAppointmentId(Long appointmentId);
}
