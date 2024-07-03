package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.StaffCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.StaffUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.BranchSummaryResponse;
import com.example.dentalclinicschedulingplatform.payload.response.StaffResponse;
import com.example.dentalclinicschedulingplatform.payload.response.StaffSummaryResponse;
import com.example.dentalclinicschedulingplatform.service.IStaffService;
import com.example.dentalclinicschedulingplatform.service.impl.AuthenticateService;
import com.example.dentalclinicschedulingplatform.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    private final IStaffService iStaffService;

    private final AuthenticateService authenticateService;


    @Operation(
            summary = "View detail staff"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'STAFF', 'CUSTOMER', 'DENTIST')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponse>> viewStaff(@PathVariable("id") Long id) {
        log.info("Has request with data: {}", id.toString());
        StaffResponse staffResponse = iStaffService.viewStaff(id);
        ApiResponse<StaffResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "View staff successfully",
                staffResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Operation(
            summary = "Create staff"
    )
    @PreAuthorize("hasAnyRole('OWNER')")
    @PostMapping()
    public ResponseEntity<ApiResponse<StaffResponse>> createStaff(@Valid @RequestBody StaffCreateRequest request) {
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
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    @PutMapping()
    public ResponseEntity<ApiResponse<StaffResponse>> updateStaff(@Valid @RequestBody StaffUpdateRequest request) {
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
    @PreAuthorize("hasAnyRole('OWNER')")
    @DeleteMapping("/{id}")
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
    @PreAuthorize("hasAnyRole('OWNER')")
    @GetMapping("/all/owner")
    public ResponseEntity<ApiResponse<List<StaffSummaryResponse>>> viewAllStaffByOwner(@RequestParam (defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
                                                                                       @RequestParam (defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size) {
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
    @PreAuthorize("hasAnyRole('OWNER')")
    @GetMapping("/all/{branchId}")
    public ResponseEntity<ApiResponse<List<StaffSummaryResponse>>> viewAllStaffByBranch(@PathVariable("branchId") Long branchId,
                                                                                        @RequestParam (defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
                                                                                        @RequestParam (defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size) {
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
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<StaffSummaryResponse>>> viewAllStaff(@RequestParam (defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
                                                                                @RequestParam (defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size) {
        List<StaffSummaryResponse> staffSummaryResponses = iStaffService.viewAll(page, size);
        ApiResponse<List<StaffSummaryResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get all staffs successfully",
                staffSummaryResponses);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Approve new staff"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/approval/{staffId}")
    public ResponseEntity<ApiResponse<StaffResponse>> approveNewStaff(@PathVariable("staffId") Long id,
                                                                      @RequestParam boolean isApproved){
        ApiResponse<StaffResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Approve staff successfully!",
                iStaffService.approveStaff(id, isApproved));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get pending staff list"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<StaffSummaryResponse>>> getPendingStaffList(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size){
        ApiResponse<Page<StaffSummaryResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get pending staff list successfully!",
                iStaffService.getStaffPendingList(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
