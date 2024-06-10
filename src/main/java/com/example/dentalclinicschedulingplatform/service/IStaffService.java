package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.CreateStaffRequest;
import com.example.dentalclinicschedulingplatform.payload.response.StaffResponse;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface IStaffService {
    ResponseEntity<StaffResponse> createStaff(CreateStaffRequest request);
}

