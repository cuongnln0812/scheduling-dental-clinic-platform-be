package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.ClinicRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.request.ClinicUpdateRequest;
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
                isApproved? "Approve clinic successfully!" : "Decline clinic successfully",
                clinicService.approveClinic(id, isApproved));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "View clinic detail information", description = "View clinic detail information" +
            "Anyone can perform this request")
    @GetMapping("/{clinicId}")
    public ResponseEntity<ApiResponse<ClinicDetailResponse>> getClinicDetail(
            @PathVariable("clinicId") Long id){
        ApiResponse<ClinicDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get clinic detail successfully!",
                clinicService.viewClinicDetail(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get pending clinic request list", description = "Get all pending clinic request for the approval action. " +
            "Only System Admin can perform this request!")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<PendingClinicListResponse>>> getPendingList(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size){
        ApiResponse<Page<PendingClinicListResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get pending clinics list successfully!",
                clinicService.getClinicPendingList(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get pending clinic detail", description = "Get pending clinic detail for the approval action. " +
            "Only System Admin can perform this request!")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/pending/{clinicId}")
    public ResponseEntity<ApiResponse<ClinicRegisterResponse>> getPendingClinicDetail(
            @PathVariable("clinicId") Long clinicId)
    {
        ApiResponse<ClinicRegisterResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get pending clinic detail successfully!",
                clinicService.getPendingClinicDetail(clinicId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update clinic information", description = "If the clinic is just approved (status = APPROVED), owner needs to pass the new working hours list " +
            "for the clinic to create and change the status to ACTIVE. Or else owner can use this to update clinic information regularly without pass the new working hours. " +
            "This API will also update the main branch information. " +
            "Only Clinic Owner can perform this request!")
    @PreAuthorize("hasAnyRole('OWNER')")
    @PutMapping()
    public ResponseEntity<ApiResponse<ClinicUpdateResponse>> updateClinic(
            @RequestBody @Valid ClinicUpdateRequest request){
        ApiResponse<ClinicUpdateResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Update clinic successfully!",
                clinicService.updateClinicInformation(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Re-activate clinic", description = "Can only re-activate clinic if its status is INACTIVE. " +
            "Only Clinic Owner can perform this request!")
    @PreAuthorize("hasAnyRole('OWNER')")
    @PutMapping("/re-activate/{clinicId}")
    public ResponseEntity<ApiResponse<ClinicDetailResponse>> reactivateClinic(
            @PathVariable("clinicId") Long clinicId){
        ApiResponse<ClinicDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Re-activate clinic successfully!",
                clinicService.reactivateClinic(clinicId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Deactivate clinic (SOFT DELETE)", description = "Can only deactivate clinic if its status is ACTIVE. " +
            "Only Clinic Owner can perform this request!")
    @PreAuthorize("hasAnyRole('OWNER')")
    @DeleteMapping("/{clinicId}")
    public ResponseEntity<ApiResponse<ClinicDetailResponse>> deactivateClinic(
            @PathVariable("clinicId") Long clinicId){
        ApiResponse<ClinicDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Deactivate clinic successfully!",
                clinicService.deactivateClinic(clinicId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get all staff and dentist")
    @PreAuthorize("hasAnyRole('OWNER')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<ClinicStaffAndDentistResponse>> getAllStaffAndDentist()
    {
        ApiResponse<ClinicStaffAndDentistResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get all staff and dentist successfully!",
                clinicService.getAllStaffAndDentistByOwner());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
