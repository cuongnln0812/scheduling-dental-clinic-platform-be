package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.request.ClinicRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.repository.ClinicRepository;
import com.example.dentalclinicschedulingplatform.service.IBranchService;
import com.example.dentalclinicschedulingplatform.service.IClinicService;
import com.example.dentalclinicschedulingplatform.service.IMailService;
import com.example.dentalclinicschedulingplatform.service.IOwnerService;
import com.example.dentalclinicschedulingplatform.utils.AutomaticGeneratedPassword;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClinicService implements IClinicService {

    private final ModelMapper modelMapper;
    private final ClinicRepository clinicRepository;
    private final IMailService mailService;
    private final IBranchService branchService;
    private final IOwnerService ownerService;

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
        clinic.setStatus(Status.PENDING);
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
            clinic.setStatus(Status.APPROVED);
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
            clinic.setStatus(Status.DENIED);
            mailService.sendClinicRequestRejectionMail(clinic.getClinicOwner().getFullName(), clinic.getClinicOwner().getEmail());
            clinicRepository.delete(clinic);
            return modelMapper.map(clinic, ApprovedClinicResponse.class);
        }
    }

    @Override
    public Page<PendingClinicListResponse> getClinicPendingList(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        Page<Clinic> clinics;
        clinics = clinicRepository.findAllByStatus(Status.PENDING, pageRequest);
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
}
