package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.ClinicRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.request.ClinicUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

public interface IClinicService {
    ClinicRegisterResponse registerClinic(ClinicRegisterRequest request);
    ApprovedClinicResponse approveClinic(Long clinicId, boolean isApproved);
    ClinicDetailResponse viewClinicDetail(Long clinicId);
    ClinicUpdateResponse updateClinicInformation(ClinicUpdateRequest request);
    Page<ClinicListResponse> getAllActiveClinic(int page, int size);
    Page<ClinicListResponse> getAllClinic(int page, int size);
    Page<PendingClinicListResponse> getClinicPendingList(int page, int size);
    ClinicRegisterResponse getPendingClinicDetail(Long clinicId);
    ClinicDetailResponse reactivateClinic(Long clinicId);
    ClinicDetailResponse deactivateClinic(Long clinicId);
    ClinicStaffAndDentistResponse getAllStaffAndDentistByOwner();
    Map<String, Object> search(int page, int size, String searchValue, String filter);
}
