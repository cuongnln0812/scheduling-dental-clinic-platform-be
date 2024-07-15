package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.WorkingHoursCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.WorkingHoursUpdateRequest;
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

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
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
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic does not exist"));

            if (currClinic != clinic) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Clinic does not belong to current owner");
            }

            WorkingHours existingDay = workingHourRepository.findByDayAndClinic(workingHour.getDay(), currClinic)
                    .orElse(null);

            if (existingDay != null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Day "+workingHour.getDay()+" is already existed");
            }

            DayInWeek.isValid(workingHour.getDay());

            if (workingHour.getStartTime() != null && workingHour.getEndTime() != null) {
                if (workingHour.getStartTime().isBefore(LocalTime.of(6, 0)) || workingHour.getEndTime().isAfter(LocalTime.of(22, 0))) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Start time must be between 06:00 AM and 22:00 PM");
                }

                if (workingHour.getStartTime().equals(workingHour.getEndTime())) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Start time must different from end time");
                }

                if (workingHour.getStartTime().isAfter(workingHour.getEndTime())) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Start time must be before end time");
                }
            }

            WorkingHours newWorkingHour = new WorkingHours();
            newWorkingHour.setDay(workingHour.getDay());

            if (workingHour.getStartTime() != null && workingHour.getEndTime() != null){
                newWorkingHour.setShift(determineShift(workingHour.getStartTime(), workingHour.getEndTime()));
                newWorkingHour.setStatus(true);
            }else {
                newWorkingHour.setShift(null);
                newWorkingHour.setStatus(false);
            }
            newWorkingHour.setStartTime(workingHour.getStartTime() != null ? workingHour.getStartTime() : null);
            newWorkingHour.setEndTime(workingHour.getEndTime() != null ? workingHour.getEndTime() : null);
//            newWorkingHour.setCreatedBy("owner4@example.com");
            newWorkingHour.setCreatedDate(LocalDateTime.now());

            newWorkingHour.setClinic(currClinic);

            workingHourRepository.save(newWorkingHour);

            slotService.generateSlotForDentalClinic(newWorkingHour.getId());

            workingHoursResponseList.add(modelMapper.map(newWorkingHour, WorkingHoursResponse.class));
        }
        return workingHoursResponseList;
    }

    @Override
    public List<WorkingHoursResponse> updateWorkingHour(List<WorkingHoursUpdateRequest> workingHours) {

        if (!authenticateService.getUserInfo().getRole().equals(UserType.OWNER.toString())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Do not have permission");
        }

        List<WorkingHoursResponse> updateWorkingHoursResponseList = new ArrayList<>();

        ClinicOwner owner = ownerRepository.findByUsername(authenticateService.getUserInfo().getUsername())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner not found"));

        Clinic ownerClinic = clinicRepository.findByClinicOwnerId(owner.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));

        for (WorkingHoursUpdateRequest updateWorkingHour: workingHours) {

            Clinic currClinic = clinicRepository.findById(updateWorkingHour.getClinicId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Inputted Clinic not found"));

            if (ownerClinic != currClinic) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Clinic does not belong to current owner");
            }

            DayInWeek.isValid(updateWorkingHour.getDay());

           if (updateWorkingHour.getStartTime() != null && updateWorkingHour.getEndTime() != null) {
               if (updateWorkingHour.getStartTime().isBefore(LocalTime.of(6, 0)) || updateWorkingHour.getEndTime().isAfter(LocalTime.of(22, 0))) {
                   throw new ApiException(HttpStatus.BAD_REQUEST, "Start time must be between 06:00 AM and 22:00 PM");
               }

               if (updateWorkingHour.getStartTime().equals(updateWorkingHour.getEndTime())) {
                   throw new ApiException(HttpStatus.BAD_REQUEST, "Start time must different from end time");
               }

               if (updateWorkingHour.getStartTime().isAfter(updateWorkingHour.getEndTime())) {
                   throw new ApiException(HttpStatus.BAD_REQUEST, "Start time must be before end time");
               }
           }

            WorkingHours currUpdateWorkingHours = workingHourRepository.findByDayAndClinic(updateWorkingHour.getDay(), currClinic)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Working hour does not exist"));

            LocalTime oldStartTime = currUpdateWorkingHours.getStartTime() != null ? currUpdateWorkingHours.getStartTime() : null;

            LocalTime oldEndTime = currUpdateWorkingHours.getEndTime() != null ? currUpdateWorkingHours.getEndTime() : null;

            currUpdateWorkingHours.setStartTime(updateWorkingHour.getStartTime() != null ? updateWorkingHour.getStartTime() : null);
            currUpdateWorkingHours.setEndTime(updateWorkingHour.getEndTime() != null ? updateWorkingHour.getEndTime() : null);

            if (updateWorkingHour.getStartTime() != null && updateWorkingHour.getEndTime() != null){
                currUpdateWorkingHours.setShift(determineShift(updateWorkingHour.getStartTime(), updateWorkingHour.getEndTime()));
                currUpdateWorkingHours.setStatus(true);
            }else {
                currUpdateWorkingHours.setShift(null);
                currUpdateWorkingHours.setStatus(false);
            }

            currUpdateWorkingHours.setModifiedDate(LocalDateTime.now());

            boolean isStartTimeDifferent = (currUpdateWorkingHours.getStartTime() == null && oldStartTime != null)
                    || (currUpdateWorkingHours.getStartTime() != null && !currUpdateWorkingHours.getStartTime().equals(oldStartTime));
            boolean isEndTimeDifferent = (currUpdateWorkingHours.getEndTime() == null && oldEndTime != null)
                    || (currUpdateWorkingHours.getEndTime() != null && !currUpdateWorkingHours.getEndTime().equals(oldEndTime));

            if (isStartTimeDifferent || isEndTimeDifferent) {
                slotService.generateSlotForUpdatingWorkingHoursDentalClinic(currUpdateWorkingHours.getId());
            }

            updateWorkingHoursResponseList.add(modelMapper.map(currUpdateWorkingHours, WorkingHoursResponse.class));
        }
        return updateWorkingHoursResponseList;
    }

    @Override
    public List<WorkingHoursResponse> viewWorkingHour(Long clinicId) {

        Clinic currClinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));

        List<WorkingHoursResponse> workingHoursResponseList = new ArrayList<>();

        List<WorkingHours> workingHours = workingHourRepository.findByClinic_ClinicId(currClinic.getClinicId());

        workingHours.sort(Comparator.comparing(WorkingHours::getDay));

        for (WorkingHours workingHour: workingHours) {
            workingHoursResponseList.add(modelMapper.map(workingHour, WorkingHoursResponse.class));
        }
        return workingHoursResponseList;
    }

    private Shift determineShift(LocalTime startTime, LocalTime endTime) {
        if (startTime.equals(LocalTime.of(6, 0)) && endTime.equals(LocalTime.of(22, 0))) {
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
