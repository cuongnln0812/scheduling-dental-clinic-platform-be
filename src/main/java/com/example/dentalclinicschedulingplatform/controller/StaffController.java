package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.entity.ClinicStaff;
import com.example.dentalclinicschedulingplatform.payload.request.CreateStaffRequest;
import com.example.dentalclinicschedulingplatform.payload.request.UpdateStaffRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.StaffResponse;
import com.example.dentalclinicschedulingplatform.payload.response.StaffSummaryResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;
import com.example.dentalclinicschedulingplatform.service.IStaffService;
import com.example.dentalclinicschedulingplatform.service.impl.AuthenticateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
@Tag(name = "Staff Controller")
@RequestMapping("/api/v1/staff")
@Validated
@Valid
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class StaffController {

    @Autowired
    private IStaffService iStaffService;

    @Autowired
    private AuthenticateService authenticateService;


    @Operation(
            summary = "View detail staff"
    )
    @GetMapping("/view/{id}")
    public ResponseEntity<ApiResponse<StaffResponse>> viewStaff(@PathVariable("id") Long id) {
        log.info("Has request with data: {}", id.toString());
        StaffResponse newStaff = iStaffService.viewStaff(id);
        ApiResponse<StaffResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "View staff successfully",
                newStaff);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Operation(
            summary = "Create staff"
    )
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<StaffResponse>> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        log.info("Has request with data: {}", request.toString());
        StaffResponse newStaff = iStaffService.createStaff(authenticateService.getUserInfo(), request);
        ApiResponse<StaffResponse> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Created staff successfully",
                newStaff);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Update staff"
    )
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<StaffResponse>> updateStaff(@Valid @RequestBody UpdateStaffRequest request) {
        log.info("Has request with data: {}", request.toString());
        StaffResponse staffResponse = iStaffService.updateStaff(authenticateService.getUserInfo(), request);
        ApiResponse<StaffResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Update staff successfully",
                staffResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete staff"
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<StaffResponse>> deleteStaff(@PathVariable("id") Long id) {
        log.info("Has request with data: {}", id.toString());
        StaffResponse staffResponse = iStaffService.deleteStaff(authenticateService.getUserInfo(), id);
        ApiResponse<StaffResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Delete staff successfully",
                staffResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "View all staff by clinic owner"
    )
    @GetMapping("/all/owner")
    public ResponseEntity<ApiResponse<List<StaffSummaryResponse>>> viewAllStaffByOwner(@RequestParam int page,
                                                                                @RequestParam int size) {
        List<StaffSummaryResponse> staffSummaryResponses = iStaffService.viewAllStaffByOwner(authenticateService.getUserInfo(), page, size);
        ApiResponse<List<StaffSummaryResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get all staffs successfully",
                staffSummaryResponses);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "View all staff by clinic branch"
    )
    @GetMapping("/all/{branchId}")
    public ResponseEntity<ApiResponse<List<StaffSummaryResponse>>> viewAllStaffByBranch(@PathVariable("branchId") Long branchId,
                                                                                @RequestParam int page,
                                                                                @RequestParam int size) {
        List<StaffSummaryResponse> staffSummaryResponses = iStaffService.viewAllStaffByClinicBranch(authenticateService.getUserInfo(), branchId, page, size);
        ApiResponse<List<StaffSummaryResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get all staffs successfully",
                staffSummaryResponses);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "View all staffs"
    )
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<StaffSummaryResponse>>> viewAllStaff(@RequestParam int page,
                                                                                @RequestParam int size) {
        List<StaffSummaryResponse> staffSummaryResponses = iStaffService.viewAll(page, size);
        ApiResponse<List<StaffSummaryResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get all staffs successfully",
                staffSummaryResponses);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
