package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.response.SlotDetailsResponse;
import com.example.dentalclinicschedulingplatform.payload.response.WorkingHoursDetailsResponse;
import com.example.dentalclinicschedulingplatform.payload.response.WorkingHoursViewResponse;

import java.time.LocalDate;
import java.util.List;

public interface ISlotService {
    void generateSlotForDentalClinic(Long workingHourId);

    List<WorkingHoursViewResponse> viewSlotListByClinicBranch(Long clinicBranchId);

    List<WorkingHoursDetailsResponse> viewAvailableSlotsByClinicBranch(LocalDate startDate, LocalDate endDate, Long clinicBranchId);

    WorkingHoursDetailsResponse viewAvailableSlotsByDateByClinicBranch(LocalDate date, Long clinicBranchId);

    void generateSlotForUpdatingWorkingHoursDentalClinic(Long workingHourId);

    SlotDetailsResponse removeSlot(Long slotId);
}
