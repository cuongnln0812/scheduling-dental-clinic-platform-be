package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.ClinicRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import org.springframework.data.domain.Page;

public interface IClinicService {
    ClinicRegisterResponse registerClinic(ClinicRegisterRequest request);
    ApprovedClinicResponse approveClinic(Long clinicId, boolean isApproved);
    Page<PendingClinicListResponse> getClinicPendingList(int page, int size);
    ClinicRegisterResponse getPendingClinicDetail(Long clinicId);
}
