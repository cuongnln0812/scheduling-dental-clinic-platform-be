package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.CreateStaffRequest;
import com.example.dentalclinicschedulingplatform.payload.request.UpdateStaffRequest;
import com.example.dentalclinicschedulingplatform.payload.response.StaffResponse;
import com.example.dentalclinicschedulingplatform.payload.response.StaffSummaryResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IStaffService {
    StaffResponse createStaff(UserInformationRes userInformationRes, CreateStaffRequest request);
    StaffResponse viewStaff(Long id);
    StaffResponse updateStaff(UserInformationRes userInformationRes, UpdateStaffRequest request);
    StaffResponse deleteStaff(UserInformationRes userInformationRes, Long id);
    List<StaffSummaryResponse> viewAllStaffByOwner(UserInformationRes userInformationRes, int page, int size);
    List<StaffSummaryResponse> viewAllStaffByClinicBranch(UserInformationRes userInformationRes, Long branchId, int page, int size);
    List<StaffSummaryResponse> viewAll(int page, int size);



}

