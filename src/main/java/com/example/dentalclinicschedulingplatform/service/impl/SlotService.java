package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.response.SlotDetailsResponse;
import com.example.dentalclinicschedulingplatform.payload.response.WorkingHoursDetailsResponse;
import com.example.dentalclinicschedulingplatform.repository.ClinicBranchRepository;
import com.example.dentalclinicschedulingplatform.repository.ClinicRepository;
import com.example.dentalclinicschedulingplatform.repository.SlotRepository;
import com.example.dentalclinicschedulingplatform.repository.WorkingHoursRepository;
import com.example.dentalclinicschedulingplatform.service.ISlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SlotService implements ISlotService {

    private final SlotRepository slotRepository;

    private final WorkingHoursRepository workingHourRepository;

    private final ClinicRepository clinicRepository;

    private final ModelMapper modelMapper;
    @Override
    public void generateSlotForDentalClinic(Long workingHourId) {
        WorkingHours workingHours = workingHourRepository.findById(workingHourId)
                .orElseThrow( () -> new ApiException(HttpStatus.BAD_REQUEST, "Working hour does not exist"));

        LocalTime slotStartTime = workingHours.getStartTime();

        int slotNo = 1;

        while (slotStartTime.isBefore(workingHours.getEndTime())) {

            LocalTime slotEndTime = slotStartTime.plusHours(1);

            if (slotEndTime.isAfter(workingHours.getEndTime())){
                break;
            }

            if (slotStartTime.equals(LocalTime.of(12, 0))){
                slotStartTime = LocalTime.of(12, 0).plusHours(1);
                continue;
            }

            Slot newSlot = new Slot();
            newSlot.setSlotNo(slotNo);
            newSlot.setStartTime(slotStartTime);
            newSlot.setEndTime(slotEndTime);
            newSlot.setStatus(true);
            newSlot.setCreatedDate(LocalDateTime.now());
            newSlot.setClinic(workingHours.getClinic());
            newSlot.setWorkingHours(workingHours);

            slotRepository.save(newSlot);
            slotNo++;
            slotStartTime = slotEndTime;
        }
    }

    @Override
    public List<WorkingHoursDetailsResponse> viewSlotListByClinicId(Long clinicId) {

        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Clinic not found"));

        List<WorkingHoursDetailsResponse> slotList = new ArrayList<>();

        for (DayInWeek day: DayInWeek.values()) {
            List<Slot> slots = slotRepository.findByClinicAnDay(day.name(), clinic.getClinicId());

            List<SlotDetailsResponse> slotDetailResponses = new ArrayList<>();

            for (Slot slot: slots) {
                slotDetailResponses.add(new SlotDetailsResponse(slot.getId(), slot.getSlotNo(), slot.getStartTime(), slot.getEndTime()));
            }

            slotList.add(new WorkingHoursDetailsResponse(day, slotDetailResponses));
        }
        return slotList;
    }
}
