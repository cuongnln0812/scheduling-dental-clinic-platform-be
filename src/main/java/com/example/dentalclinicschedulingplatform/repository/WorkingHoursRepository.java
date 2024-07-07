package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.Clinic;
import com.example.dentalclinicschedulingplatform.entity.DayInWeek;
import com.example.dentalclinicschedulingplatform.entity.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {
    Optional<WorkingHours> findByDayAndClinic(DayInWeek day, Clinic clinic);
    List<WorkingHours> findByClinic_ClinicId(Long clinicId);
}
