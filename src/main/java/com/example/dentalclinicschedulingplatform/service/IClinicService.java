package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.ClinicRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApprovedClinicResponse;
import com.example.dentalclinicschedulingplatform.payload.response.ClinicListResponse;
import com.example.dentalclinicschedulingplatform.payload.response.ClinicRegisterResponse;
import org.springframework.data.domain.Page;

public interface IClinicService {
    ClinicRegisterResponse registerClinic(ClinicRegisterRequest request);
    ApprovedClinicResponse approveClinic(Long clinicId, boolean isApproved);
    Page<ClinicListResponse> getClinicPendingList(int page, int size);
}
