package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.ClinicRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.service.IClinicService;
import com.example.dentalclinicschedulingplatform.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clinics")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Clinic Controller")
@RequiredArgsConstructor
public class ClinicController {

    private final IClinicService clinicService;

    @Operation(summary = "Register clinic to platform", description = "Register clinic and set status as PENDING. " +
            "Anonymous or customer can perform this request!")
    @PostMapping("/registration")
    public ResponseEntity<ApiResponse<ClinicRegisterResponse>> registerClinic(
            @Valid @RequestBody ClinicRegisterRequest request){
        ApiResponse<ClinicRegisterResponse> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Register clinic successfully!",
                clinicService.registerClinic(request));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Approve clinic request", description = "Approve clinic request by set the isApproved as true and " +
            "send a confirmation email to the owner's Email with providing account username/pass. Or if rejected, system will" +
            "send a rejected email. " +
            "Only System Admin can perform this request!")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/approval/{clinicId}")
    public ResponseEntity<ApiResponse<ApprovedClinicResponse>> approveClinic(
            @PathVariable("clinicId") Long id, @RequestParam boolean isApproved){
        ApiResponse<ApprovedClinicResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Approve clinic successfully!",
                clinicService.approveClinic(id, isApproved));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get pending clinic request list", description = "Get all pending clinic request for the approval action. " +
            "Only System Admin can perform this request!")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<ClinicListResponse>>> getPendingList(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size){
        ApiResponse<Page<ClinicListResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get pending clinics list successfully!",
                clinicService.getClinicPendingList(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
