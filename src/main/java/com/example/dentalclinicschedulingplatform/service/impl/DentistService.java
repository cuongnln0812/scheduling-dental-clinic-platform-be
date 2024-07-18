package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.request.DentistCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.DentistUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.DentistDetailResponse;
import com.example.dentalclinicschedulingplatform.payload.response.DentistListResponse;
import com.example.dentalclinicschedulingplatform.payload.response.DentistViewListResponse;
import com.example.dentalclinicschedulingplatform.repository.*;
import com.example.dentalclinicschedulingplatform.service.IDentistService;
import com.example.dentalclinicschedulingplatform.service.IMailService;
import com.example.dentalclinicschedulingplatform.utils.AutomaticGeneratedPassword;
import com.google.api.Http;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DentistService implements IDentistService  {

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final DentistRepository dentistRepository;
    private final OwnerRepository ownerRepository;
    private final ClinicBranchRepository branchRepository;
    private final IMailService mailService;
    private final AppointmentRepository appointmentRepository;
    private final SlotRepository slotRepository;

    @Override
    public Page<DentistListResponse> getDentistListByBranch(Long branchId, int page, int size, String dir, String by) {
        Sort sort = dir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(by).ascending() : Sort.by(by).descending();
        Pageable pageRequest = PageRequest.of(page, size, sort);
        Page<Dentist> dentistList;
        if(branchId != null){
            dentistList = dentistRepository.findAllByClinicBranch_BranchId(branchId, pageRequest);
        }else dentistList = dentistRepository.findAll(pageRequest);

        return dentistList.map(this::mapListRes);
    }

    @Override
    public Page<DentistListResponse> getDentistListByClinic(Long id, int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        Page<Dentist> dentistList;
        if(id != null){
            dentistList = dentistRepository.findByClinicId(id, pageRequest);
        }else throw new ResourceNotFoundException("Clinic", "id", id);
        return dentistList.map(this::mapListRes);
    }

    @Override
    public Page<DentistListResponse> getPendingDentistList(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        Page<Dentist> dentistList;
        dentistList = dentistRepository.findAllByStatus(ClinicStatus.PENDING, pageRequest);
        return dentistList.map(this::mapListRes);
    }

    @Override
    @Transactional
    public DentistDetailResponse approveDentistAccount(Long id, boolean isApproved) {
        Dentist dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist", "id", id));
        if (!dentist.getStatus().equals(ClinicStatus.PENDING))
            throw new ApiException(HttpStatus.CONFLICT, "Dentist status must be pending to be approved");
        if(isApproved) {
            dentist.setStatus(ClinicStatus.ACTIVE);
            String randomPassword = AutomaticGeneratedPassword.generateRandomPassword();
            dentist.setUsername("dentist" + dentist.getId());
            dentist.setPassword(passwordEncoder.encode(randomPassword));
            dentist = dentistRepository.save(dentist);
            ClinicOwner owner = dentist.getClinicBranch().getClinic().getClinicOwner();
            mailService.sendDentistRequestApprovalMail(dentist, randomPassword, owner);
            return mapDetailRes(dentist);
        }else {
            dentist.setStatus(ClinicStatus.DENIED);
            dentistRepository.delete(dentist);
            return mapDetailRes(dentist);
        }
    }


    @Override
    public DentistDetailResponse getDentistDetail(Long id) {
        Dentist dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist", "id", id));
        return mapDetailRes(dentist);
    }

    @Override
    @Transactional
    public DentistDetailResponse createDentist(DentistCreateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ClinicOwner owner = ownerRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "System cannot find the owner who currently logged in!"));
        if(dentistRepository.existsByEmail(request.getEmail()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already used");
        if(request.getDob().isAfter(LocalDate.now()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Dob cannot be after present date!");
        if(Period.between(request.getDob(), LocalDate.now()).getYears() < 18)
            throw new ApiException(HttpStatus.BAD_REQUEST, "Dob must be over or equals 18 years old!");
        if(dentistRepository.existsByPhone(request.getPhone())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Phone is already used");
        }
        ClinicBranch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic Branch", "id", request.getBranchId()));

        Dentist dentist = new Dentist();
//        modelMapper.map(request, dentist);
        dentist.setFullName(request.getFullName());
        dentist.setEmail(request.getEmail());
        dentist.setGender(request.getGender());
        dentist.setPhone(request.getPhone());
        dentist.setDob(request.getDob());
        dentist.setAddress(request.getAddress());
        dentist.setDescription(request.getDescription());
        dentist.setSpecialty(request.getSpecialty());
        dentist.setExperience(request.getExperience());
        dentist.setAvatar(request.getAvatar());
        dentist.setStatus(ClinicStatus.PENDING);
        dentist.setClinicBranch(branch);
        dentist = dentistRepository.save(dentist);
//        branch.getDentists().add(dentist);
//        branchRepository.save(branch);
        mailService.senDentistRequestConfirmationMail(dentist, owner);
        return mapDetailRes(dentist);
    }

    @Override
    @Transactional
    public DentistDetailResponse updateDentistDetails(DentistUpdateRequest request) {
        Dentist existingDentist = dentistRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist", "id", request.getId()));
        if(!existingDentist.getUsername().equals(request.getUsername()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username cannot be changed!");
        if(dentistRepository.existsByEmail(request.getEmail()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already used");
        if(request.getDob().isAfter(LocalDate.now()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Date of birth cannot be after present date!");
        if(Period.between(request.getDob(), LocalDate.now()).getYears() < 18)
            throw new ApiException(HttpStatus.BAD_REQUEST, "You must be over or equals 18 years old!");
        if(dentistRepository.existsByPhone(request.getPhone())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Phone is already used");
        }
        ClinicStatus.isValid(request.getStatus());
        if(existingDentist.getStatus().equals(ClinicStatus.PENDING) || existingDentist.getStatus().equals(ClinicStatus.DENIED))
            throw new ApiException(HttpStatus.CONFLICT, "ClinicStatus cannot be changed");
        ClinicBranch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic Branch", "id", request.getBranchId()));
        modelMapper.map(request, existingDentist);
        existingDentist.setClinicBranch(branch);
        Dentist updatedDentist = dentistRepository.save(existingDentist);
        return mapDetailRes(updatedDentist);
    }

    @Override
    @Transactional
    public DentistDetailResponse removeDentist(Long id) {
        Dentist existingDentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist", "id", id));
        if(existingDentist.getStatus().equals(ClinicStatus.ACTIVE)) {
            existingDentist.setStatus(ClinicStatus.INACTIVE);
        }else throw new ApiException(HttpStatus.CONFLICT, "Account is not able to remove because it may in INACTIVE or PENDING status");
        Dentist updatedDentist = dentistRepository.save(existingDentist);
        return mapDetailRes(updatedDentist);
    }

    @Override
    public DentistDetailResponse reactivateDentist(Long id) {
        Dentist existingDentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist", "id", id));
        if(existingDentist.getStatus().equals(ClinicStatus.INACTIVE)) {
            existingDentist.setStatus(ClinicStatus.ACTIVE);
        }else throw new ApiException(HttpStatus.CONFLICT, "Account is not able to re-activate because it's not currently in INACTIVE status");
        Dentist updatedDentist = dentistRepository.save(existingDentist);
        return mapDetailRes(updatedDentist);
    }

    @Override
    public List<DentistViewListResponse> getAvailableDentistOfDateByBranch(Long branchId, LocalDate date, Long slotId) {

        ClinicBranch clinicBranch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch not found"));

        Slot currSlot = slotRepository.findById(slotId)
                .orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

        List<Appointment> appointments = appointmentRepository.findByDateAndSlotOfClinicBranch(date, branchId, slotId);

        List<DentistViewListResponse> availableDentists = new ArrayList<>();

        List<Dentist> occupiedDentists = appointments.stream()
                .map(Appointment::getDentist)
                .collect(Collectors.toList());

        List<Dentist> dentistList = dentistRepository.findAllByClinicBranch_BranchId(clinicBranch.getBranchId());

        for (Dentist dentist: dentistList) {
            if (!occupiedDentists.contains(dentist) && dentist.getStatus().equals(ClinicStatus.ACTIVE)){
                availableDentists.add(modelMapper.map(dentist, DentistViewListResponse.class));
            }
        }
        return availableDentists;
    }

    @Override
    public List<DentistViewListResponse> getAvailableDentistOfDateByBranchForUpdatingAppointment(Long branchId, LocalDate date, Long slotId, Long appointmentId) {
        ClinicBranch clinicBranch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch not found"));

        Slot currSlot = slotRepository.findById(slotId)
                .orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

        Appointment updatingAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment for updating not found"));

        List<Appointment> appointments = appointmentRepository.findByDateAndSlotOfClinicBranch(date, branchId, slotId);

        List<DentistViewListResponse> availableDentists = new ArrayList<>();

        List<Dentist> occupiedDentists = appointments.stream()
                .filter(appointment -> !appointment.equals(updatingAppointment))
                .map(Appointment::getDentist)
                .collect(Collectors.toList());

        List<Dentist> dentistList = dentistRepository.findAllByClinicBranch_BranchId(clinicBranch.getBranchId());

        for (Dentist dentist: dentistList) {
            if (!occupiedDentists.contains(dentist) && dentist.getStatus().equals(ClinicStatus.ACTIVE)){
                availableDentists.add(modelMapper.map(dentist, DentistViewListResponse.class));
            }
        }
        return availableDentists;
    }

    private DentistDetailResponse mapDetailRes(Dentist dentist){
        DentistDetailResponse res = modelMapper.map(dentist, DentistDetailResponse.class);
        res.setCity(dentist.getClinicBranch().getCity());
        res.setClinicId(dentist.getClinicBranch().getClinic().getClinicId());
        res.setClinicName(dentist.getClinicBranch().getClinic().getClinicName());
        return res;
    }

    private DentistListResponse mapListRes(Dentist dentist){
        DentistListResponse res = modelMapper.map(dentist, DentistListResponse.class);
        res.setCity(dentist.getClinicBranch().getCity());
        res.setClinicName(dentist.getClinicBranch().getClinic().getClinicName());
        return res;
    }
}
