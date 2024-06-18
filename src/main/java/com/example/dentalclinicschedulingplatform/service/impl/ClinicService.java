package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.Clinic;
import com.example.dentalclinicschedulingplatform.entity.ClinicOwner;
import com.example.dentalclinicschedulingplatform.entity.Status;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.request.ClinicRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ClinicRegisterResponse;
import com.example.dentalclinicschedulingplatform.repository.ClinicRepository;
import com.example.dentalclinicschedulingplatform.service.IBranchService;
import com.example.dentalclinicschedulingplatform.service.IClinicService;
import com.example.dentalclinicschedulingplatform.service.IOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClinicService implements IClinicService {

    private final ClinicRepository clinicRepository;
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
        ClinicOwner tmpOwner = ownerService.registerOwnerFromRequest(request.getOwnerRegisterRequest());
        clinic.setClinicOwner(tmpOwner);
        Clinic tmpClinic = clinicRepository.save(clinic);
        return getClinicRegisterResponse(tmpClinic, tmpOwner);
    }

    @Override
    public void approveClinic(Long clinicId) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", clinicId));
        clinic.setStatus(Status.APPROVED);
        Clinic savedClinic = clinicRepository.save(clinic);
        branchService.createMainBranch(savedClinic);
    }

    private ClinicRegisterResponse getClinicRegisterResponse(Clinic tmpClinic, ClinicOwner tmpOwner) {
        ClinicRegisterResponse response = new ClinicRegisterResponse();
        response.setClinicId(tmpClinic.getClinicId());
        response.setAddress(tmpClinic.getAddress());
        response.setCity(tmpClinic.getCity());
        response.setPhone(tmpClinic.getPhone());
        response.setClinicRegistration(tmpClinic.getClinicRegistration());
        response.setWebsiteUrl(tmpClinic.getWebsiteUrl());
        response.setClinicImage(tmpClinic.getClinicImage());
        response.setFullName(tmpOwner.getFullName());
        response.setEmail(tmpOwner.getEmail());
        response.setClinicPhone(tmpOwner.getPhone());
        return response;
    }


}
