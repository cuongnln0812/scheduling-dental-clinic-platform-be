package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.response.SlotDetailsResponse;
import com.example.dentalclinicschedulingplatform.payload.response.WorkingHoursDetailsResponse;
import com.example.dentalclinicschedulingplatform.payload.response.WorkingHoursViewResponse;
import com.example.dentalclinicschedulingplatform.repository.*;
import com.example.dentalclinicschedulingplatform.service.ISlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    private final AppointmentRepository appointmentRepository;

    private final ClinicBranchRepository clinicBranchRepository;

    private final AuthenticateService authenticateService;

    private final OwnerRepository ownerRepository;

    private final ModelMapper modelMapper;
    @Override
    public void generateSlotForDentalClinic(Long workingHourId) {
        WorkingHours workingHours = workingHourRepository.findById(workingHourId)
                .orElseThrow( () -> new ApiException(HttpStatus.BAD_REQUEST, "Working hour does not exist"));

        generateSlots(workingHours);
    }

    @Override
    public List<WorkingHoursViewResponse> viewSlotListByClinicBranch(Long clinicBranchId) {

        ClinicBranch clinicBranch = clinicBranchRepository.findById(clinicBranchId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch not found"));

        Clinic clinic = clinicRepository.findById(clinicBranch.getClinic().getClinicId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Clinic not found"));

        List<WorkingHoursViewResponse> slotList = new ArrayList<>();

        for (DayInWeek day: DayInWeek.values()) {
            List<Slot> slots = slotRepository.findByClinicAndDay(day.name(), clinic.getClinicId());

            WorkingHours existingDay = workingHourRepository.findByDayAndClinic(day, clinic)
                    .orElse(null);

            List<SlotDetailsResponse> slotDetailResponses = new ArrayList<>();

            for (Slot slot: slots) {
                slotDetailResponses.add(new SlotDetailsResponse(slot.getId(), slot.getSlotNo(), slot.getStartTime(), slot.getEndTime(), slot.isStatus()));
            }

            if (existingDay != null) {
                slotList.add(new WorkingHoursViewResponse(day.name(),existingDay.getCreatedDate(), existingDay.getModifiedDate(),slotDetailResponses));
            }else {
                slotList.add(new WorkingHoursViewResponse(day.name(),null, null,slotDetailResponses));
            }
        }
        return slotList;
    }

    @Override
    public List<WorkingHoursDetailsResponse> viewAvailableSlotsByClinicBranch(LocalDate startDate, LocalDate endDate, Long clinicBranchId) {
        List<Appointment> appointments = appointmentRepository.findByStartDateAndEndDateOfClinicBranch(startDate, endDate, clinicBranchId);

        List<Slot> bookedSlots = new ArrayList<>();

        ClinicBranch branch = clinicBranchRepository.findById(clinicBranchId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch not found"));

        Clinic clinic = clinicRepository.findById(branch.getClinic().getClinicId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));

        for (Appointment appointment: appointments) {
            Slot bookedSlot = slotRepository.findById(appointment.getSlot().getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

            bookedSlots.add(bookedSlot);
        }

        List<WorkingHoursDetailsResponse> availableSlots = new ArrayList<>();

        List<String> days = getDaysBetween(startDate, endDate);

        for (String day: days) {
            List<Slot> slots = slotRepository.findByClinicAndDay(day, clinic.getClinicId());
            List<SlotDetailsResponse> slotDetailResponses = new ArrayList<>();

            for (Slot slot: slots) {
                if (!bookedSlots.contains(slot)) {
                    slotDetailResponses.add(new SlotDetailsResponse(slot.getId(), slot.getSlotNo(), slot.getStartTime(), slot.getEndTime(), slot.isStatus()));
                }
            }

            availableSlots.add(new WorkingHoursDetailsResponse(day, slotDetailResponses));
        }
        return availableSlots;
    }

    @Override
    public WorkingHoursDetailsResponse viewAvailableSlotsByDateByClinicBranch(LocalDate date, Long clinicBranchId) {
        List<Appointment> appointments = appointmentRepository.findByDateOfClinicBranch(date, clinicBranchId);

        ClinicBranch clinicBranch = clinicBranchRepository.findById(clinicBranchId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch not found"));

        Clinic clinic = clinicRepository.findById(clinicBranch.getClinic().getClinicId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));

        List<Slot> bookedSlots = new ArrayList<>();

        for (Appointment appointment: appointments) {
            Slot slot = slotRepository.findById(appointment.getSlot().getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

            bookedSlots.add(slot);
        }

        List<WorkingHoursDetailsResponse> availableSlotsByDate = new ArrayList<>();

        List<Slot> slots = slotRepository.findByClinicAndDay(date.getDayOfWeek().toString().toUpperCase(), clinic.getClinicId());

        List<SlotDetailsResponse> slotDetailResponses = new ArrayList<>();

        for (Slot slot :slots) {
            if (!bookedSlots.contains(slot)){
                slotDetailResponses.add(new SlotDetailsResponse(slot.getId(), slot.getSlotNo(), slot.getStartTime(), slot.getEndTime(), slot.isStatus()));
            }
        }

        return new WorkingHoursDetailsResponse(date.getDayOfWeek().toString(), slotDetailResponses);
    }

    @Override
    public void generateSlotForUpdatingWorkingHoursDentalClinic(Long workingHourId) {
        WorkingHours workingHours = workingHourRepository.findById(workingHourId)
                .orElseThrow( () -> new ApiException(HttpStatus.BAD_REQUEST, "Working hour does not exist"));

        List<Slot> slots = slotRepository.findByClinicAndDay(workingHours.getDay().name(), workingHours.getClinic().getClinicId());

        for (Slot slot: slots) {
            slot.setStatus(false);
            slot.setModifiedDate(LocalDateTime.now());
            slotRepository.save(slot);
        }

        generateSlots(workingHours);
    }

    @Override
    public SlotDetailsResponse removeSlot(Long slotId) {
        if (!authenticateService.getUserInfo().getRole().equals(UserType.OWNER.toString())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Do not have permission");
        }

        ClinicOwner owner = ownerRepository.findByUsername(authenticateService.getUserInfo().getUsername())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner not found"));

        Clinic clinic = clinicRepository.findByClinicOwnerId(owner.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));

        Slot currSlot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

        if (currSlot.getClinic().getClinicId() != clinic.getClinicId()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Slot does not belong to current clinic");
        }

        if (!currSlot.isStatus()){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Slot removed already");
        }

        currSlot.setStatus(false);

        slotRepository.save(currSlot);

        return modelMapper.map(currSlot, SlotDetailsResponse.class);
    }

    @Override
    public WorkingHoursDetailsResponse viewAvailableSlotsByDateByClinicBranchForUpdatingAppointment(LocalDate date, Long clinicBranchId, Long appointmentId) {

        List<Appointment> appointments = appointmentRepository.findByDateOfClinicBranch(date, clinicBranchId);

        Appointment updatingAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment for updating not found"));

        ClinicBranch clinicBranch = clinicBranchRepository.findById(clinicBranchId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch not found"));

        Clinic clinic = clinicRepository.findById(clinicBranch.getClinic().getClinicId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));

        List<Slot> bookedSlots = new ArrayList<>();

        for (Appointment appointment: appointments) {
            if (!appointment.equals(updatingAppointment)){
                Slot slot = slotRepository.findById(appointment.getSlot().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

                bookedSlots.add(slot);
            }
        }

        List<WorkingHoursDetailsResponse> availableSlotsByDate = new ArrayList<>();

        List<Slot> slots = slotRepository.findByClinicAndDay(date.getDayOfWeek().toString().toUpperCase(), clinic.getClinicId());

        List<SlotDetailsResponse> slotDetailResponses = new ArrayList<>();

        for (Slot slot :slots) {
            if (!bookedSlots.contains(slot)){
                slotDetailResponses.add(new SlotDetailsResponse(slot.getId(), slot.getSlotNo(), slot.getStartTime(), slot.getEndTime(), slot.isStatus()));
            }
        }

        return new WorkingHoursDetailsResponse(date.getDayOfWeek().toString(), slotDetailResponses);
    }

    public List<String> getDaysBetween(LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate;
        List<String> days = new ArrayList<>();

        while (!start.isAfter(endDate)){
            days.add(start.getDayOfWeek().toString().toUpperCase());
            start = start.plusDays(1);
        }

        return days;
    }

    public void generateSlots(WorkingHours workingHours) {

        if (workingHours.getStartTime() == null){
            return;
        }

        LocalTime slotStartTime = workingHours.getStartTime();

        int slotNo = 1;

        while (slotStartTime.isBefore(workingHours.getEndTime())) {

            LocalTime slotEndTime = slotStartTime.plusHours(1);

            if (slotEndTime.isAfter(workingHours.getEndTime())){
                break;
            }

            if (slotStartTime.equals(LocalTime.of(12, 0))
                    || slotStartTime.equals(LocalTime.of(12, 15))
                    || slotStartTime.equals(LocalTime.of(12, 30))
                    || slotStartTime.equals(LocalTime.of(12, 45))){
                slotStartTime = LocalTime.of(slotStartTime.getHour(), slotStartTime.getMinute()).plusHours(1);
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
}
