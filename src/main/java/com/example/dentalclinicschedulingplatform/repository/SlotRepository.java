package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    @Override
    Optional<Slot> findById(Long slotId);

    @Query(value = "SELECT s.* FROM slot s join working_hours wh on s.working_hours_id = wh.working_hours_id" +
            " where s.clinic_Id = :clinicId and wh.day = :day and s.status = true", nativeQuery = true)
    List<Slot> findByClinicAndDay(String day, Long clinicId);

    @Query(value = "SELECT s.slot_id FROM slot s join working_hours wh on s.working_hours_id = wh.working_hours_id " +
            " where s.clinic_id = :clinicId and wh.day = :day and s.status = true", nativeQuery = true)
    List<Long> findSlotIdByClinic(String day,Long clinicId);
}
