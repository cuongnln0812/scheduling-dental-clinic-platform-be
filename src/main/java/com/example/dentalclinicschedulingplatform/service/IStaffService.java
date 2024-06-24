package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.StaffCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.StaffUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.StaffResponse;
import com.example.dentalclinicschedulingplatform.payload.response.StaffSummaryResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IStaffService {
    StaffResponse createStaff(UserInformationRes userInformationRes, StaffCreateRequest request);
    StaffResponse viewStaff(Long id);
    StaffResponse updateStaff(UserInformationRes userInformationRes, StaffUpdateRequest request);
    StaffResponse deleteStaff(UserInformationRes userInformationRes, Long id);
    List<StaffSummaryResponse> viewAllStaffByOwner(UserInformationRes userInformationRes, int page, int size);
    List<StaffSummaryResponse> viewAllStaffByClinicBranch(UserInformationRes userInformationRes, Long branchId, int page, int size);
    List<StaffSummaryResponse> viewAll(int page, int size);
    StaffResponse approveStaff(Long id, boolean isApproved);
    Page<StaffSummaryResponse> getStaffPendingList(int page, int size);



}

