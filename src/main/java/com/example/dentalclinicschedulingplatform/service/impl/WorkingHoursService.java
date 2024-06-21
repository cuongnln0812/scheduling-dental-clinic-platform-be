package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.WorkingHoursCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.WorkingHoursResponse;
import com.example.dentalclinicschedulingplatform.repository.ClinicBranchRepository;
import com.example.dentalclinicschedulingplatform.repository.ClinicRepository;
import com.example.dentalclinicschedulingplatform.repository.OwnerRepository;
import com.example.dentalclinicschedulingplatform.repository.WorkingHoursRepository;
import com.example.dentalclinicschedulingplatform.service.IWorkingHoursService;
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
public class WorkingHoursService implements IWorkingHoursService {

    private final WorkingHoursRepository workingHourRepository;

    private final AuthenticateService authenticateService;

    private final ClinicBranchRepository branchRepository;

    private final OwnerRepository ownerRepository;

    private final ClinicRepository clinicRepository;

    private final SlotService slotService;

    private final ModelMapper modelMapper;
    @Override
    public List<WorkingHoursResponse> createWorkingHour(List<WorkingHoursCreateRequest> workingHours) {
        if (!authenticateService.getUserInfo().getRole().equals(UserType.OWNER.toString())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Do not have permission");
        }

        ClinicOwner owner = ownerRepository.findByUsernameOrEmail(authenticateService.getUserInfo().getUsername(), authenticateService.getUserInfo().getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner not found"));

        Clinic clinic = clinicRepository.findByClinicOwnerId(owner.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));

        List<WorkingHoursResponse> workingHoursResponseList = new ArrayList<>();

        for (WorkingHoursCreateRequest workingHour: workingHours) {

            Clinic currClinic = clinicRepository.findById(workingHour.getClinicId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic branch does not exist"));

            if (currClinic != clinic) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Clinic does not belong to current owner");
            }

            WorkingHours existingDay = workingHourRepository.findByDayAndClinic(workingHour.getDay(), currClinic)
                    .orElse(null);

            if (existingDay != null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Day "+workingHour.getDay()+" is already existed");
            }

            DayInWeek.isValid(workingHour.getDay());

            if (workingHour.getStartTime().isBefore(LocalTime.of(7, 0)) || workingHour.getEndTime().isAfter(LocalTime.of(21, 0))) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Start time must be between 07:00 AM and 21:00 PM");
            }

            if (workingHour.getStartTime().equals(workingHour.getEndTime())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Start time must different from end time");
            }

            if (workingHour.getStartTime().isAfter(workingHour.getEndTime())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Start time must be before end time");
            }

            WorkingHours newWorkingHour = new WorkingHours();
            newWorkingHour.setDay(workingHour.getDay());
            newWorkingHour.setShift(determineShift(workingHour.getStartTime(), workingHour.getEndTime()));
            newWorkingHour.setStartTime(workingHour.getStartTime());
            newWorkingHour.setEndTime(workingHour.getEndTime());
//            newWorkingHour.setCreatedBy("owner4@example.com");
            newWorkingHour.setCreatedDate(LocalDateTime.now());
            newWorkingHour.setStatus(true);
            newWorkingHour.setClinic(currClinic);

            workingHourRepository.save(newWorkingHour);

            slotService.generateSlotForDentalClinic(newWorkingHour.getId());

            workingHoursResponseList.add(modelMapper.map(newWorkingHour, WorkingHoursResponse.class));
        }
        return workingHoursResponseList;
    }

    private Shift determineShift(LocalTime startTime, LocalTime endTime) {
        if (startTime.equals(LocalTime.of(7, 0)) && endTime.equals(LocalTime.of(21, 0))) {
            return Shift.ALLDAY;
        } else if (startTime.isBefore(LocalTime.of(12, 0)) && endTime.isAfter(LocalTime.of(12, 0))) {
            return Shift.ALLDAY;
        } else if (startTime.isBefore(LocalTime.of(12, 0))) {
            return Shift.MORNING;
        } else {
            return Shift.AFTERNOON;
        }
    }
}
