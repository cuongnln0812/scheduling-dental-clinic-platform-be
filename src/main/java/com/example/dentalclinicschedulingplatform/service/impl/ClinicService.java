package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.request.ClinicRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.repository.ClinicBranchRepository;
import com.example.dentalclinicschedulingplatform.repository.ClinicRepository;
import com.example.dentalclinicschedulingplatform.repository.OwnerRepository;
import com.example.dentalclinicschedulingplatform.service.*;
import com.example.dentalclinicschedulingplatform.utils.AutomaticGeneratedPassword;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicService implements IClinicService {

    private final ModelMapper modelMapper;
    private final ClinicRepository clinicRepository;
    private final OwnerRepository ownerRepository;
    private final ClinicBranchRepository branchRepository;
    private final IMailService mailService;
    private final IBranchService branchService;
    private final IOwnerService ownerService;
    private final IAuthenticateService authenticateService;

    @Override
    @Transactional
    public ClinicRegisterResponse registerClinic(ClinicRegisterRequest request) {
        Clinic clinic = new Clinic();
        clinic.setClinicName(request.getClinicName());
        clinic.setAddress(request.getAddress());
        clinic.setCity(request.getCity());
        clinic.setPhone(request.getClinicPhone());
        clinic.setClinicRegistration(request.getClinicRegistration());
        clinic.setWebsiteUrl(clinic.getWebsiteUrl());
        clinic.setClinicImage(clinic.getClinicImage());
        clinic.setStatus(ClinicStatus.PENDING);
        ClinicOwner tmpOwner = ownerService.registerOwnerFromRequest(request.getOwnerInformation());
        clinic.setClinicOwner(tmpOwner);
        Clinic tmpClinic = clinicRepository.save(clinic);
        mailService.sendClinicRequestConfirmationMail(tmpOwner.getFullName(), tmpOwner.getEmail());
        return getClinicRegisterResponse(tmpClinic, tmpOwner);
    }

    @Override
    public ApprovedClinicResponse approveClinic(Long clinicId, boolean isApproved) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", clinicId));
        if(isApproved) {
            clinic.setStatus(ClinicStatus.APPROVED);
            Clinic savedClinic = clinicRepository.save(clinic);
            ClinicBranch approvedBranch = branchService.createMainBranch(savedClinic);
            if (savedClinic.getClinicOwner() == null)
                throw new ApiException(HttpStatus.CONFLICT, "This clinic does not have its owner");
            String randomPassword = AutomaticGeneratedPassword.generateRandomPassword();
            ClinicOwner approvedOwner = ownerService.approveOwnerAccount(savedClinic.getClinicOwner().getId(), randomPassword);
            mailService.sendClinicRequestApprovalMail(approvedOwner, randomPassword);

            ApprovedOwnerResponse ownerResponse = modelMapper.map(approvedOwner, ApprovedOwnerResponse.class);
            ApprovedBranchResponse branchResponse = modelMapper.map(approvedBranch, ApprovedBranchResponse.class);
            ApprovedClinicResponse clinicResponse = modelMapper.map(savedClinic, ApprovedClinicResponse.class);
            clinicResponse.setOwnerDetail(ownerResponse);
            clinicResponse.setBranchDetail(branchResponse);
            return clinicResponse;
        }else {
            clinic.setStatus(ClinicStatus.DENIED);
            mailService.sendClinicRequestRejectionMail(clinic.getClinicOwner().getFullName(), clinic.getClinicOwner().getEmail());
            clinicRepository.delete(clinic);
            return modelMapper.map(clinic, ApprovedClinicResponse.class);
        }
    }

    @Override
    public Page<PendingClinicListResponse> getClinicPendingList(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        Page<Clinic> clinics;
        clinics = clinicRepository.findAllByStatus(ClinicStatus.PENDING, pageRequest);
        return clinics.map(clinic -> new PendingClinicListResponse(clinic.getClinicId(),
                clinic.getClinicName(), clinic.getClinicOwner().getFullName()));
    }

    @Override
    public ClinicRegisterResponse getPendingClinicDetail(Long clinicId) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", clinicId));
        return getClinicRegisterResponse(clinic, clinic.getClinicOwner());
    }

    private ClinicRegisterResponse getClinicRegisterResponse(Clinic tmpClinic, ClinicOwner tmpOwner) {
        ClinicRegisterResponse response = new ClinicRegisterResponse();
        response.setClinicId(tmpClinic.getClinicId());
        response.setClinicName(tmpClinic.getClinicName());
        response.setAddress(tmpClinic.getAddress());
        response.setCity(tmpClinic.getCity());
        response.setPhone(tmpClinic.getPhone());
        response.setClinicRegistration(tmpClinic.getClinicRegistration());
        response.setWebsiteUrl(tmpClinic.getWebsiteUrl());
        response.setClinicImage(tmpClinic.getClinicImage());
        response.setClinicStatus(tmpClinic.getStatus());
        response.setOwnerDetail(modelMapper.map(tmpOwner, OwnerRegisterResponse.class));
        return response;
    }

    @Override
    public ClinicStaffAndDentistResponse getAllStaffAndDentistByOwner() {
        try {
            ClinicOwner clinicOwner = ownerRepository.findByUsername(authenticateService.getUserInfo().getUsername())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic owner not found"));

            Clinic clinic = clinicRepository.findByClinicOwnerId(clinicOwner.getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));

            List<ClinicBranch> clinicBranchList = branchRepository.findAllByClinic_ClinicId(clinic.getClinicId());

            // Lấy danh sách staff từ clinicBranchList
            List<ClinicStaff> staffList = clinicBranchList.stream()
                    .flatMap(branch -> branch.getStaffs().stream())
                    .collect(Collectors.toList());

            // Chuyển đổi danh sách ClinicStaff sang StaffSummaryResponse
            List<StaffSummaryResponse> staffSummaryResponses = staffList.stream()
                    .map(StaffSummaryResponse::new)
                    .collect(Collectors.toList());

            // Lấy danh sách dentist từ clinicBranchList
            List<Dentist> dentistList = clinicBranchList.stream()
                    .flatMap(branch -> branch.getDentists().stream())
                    .collect(Collectors.toList());

            // Chuyển đổi danh sách Dentist sang DentistListResponse
            List<DentistListResponse> dentistListResponses = dentistList.stream()
                    .map(dentist -> new DentistListResponse(
                            dentist.getId(),
                            dentist.getFullName(),
                            dentist.getUsername(),
                            dentist.getEmail(),
                            dentist.getPhone(),
                            dentist.getDob(),
                            dentist.getGender(),
                            dentist.getClinicBranch().getBranchName(),
                            dentist.getStatus()))
                    .collect(Collectors.toList());

            return new ClinicStaffAndDentistResponse(staffSummaryResponses, dentistListResponses);
        } catch (Exception e) {
            throw e;
        }
    }
}
