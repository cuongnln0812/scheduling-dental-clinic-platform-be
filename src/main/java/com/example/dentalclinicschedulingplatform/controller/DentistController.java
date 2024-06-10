package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.DentistCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.DentistUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.DentistDetailResponse;
import com.example.dentalclinicschedulingplatform.payload.response.DentistListResponse;
import com.example.dentalclinicschedulingplatform.service.IDentistService;
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
@RequestMapping("/api/v1/dentists")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dentist Controller")
@RequiredArgsConstructor
public class DentistController {

    private final IDentistService dentistService;

    @Operation(summary = "Create dentist account", description = "Create dentist account but only set status as PENDING. " +
    "Only System Admin, Clinic Owner, Clinic Staff can perform this request!")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'STAFF')")
    @PostMapping()
    public ResponseEntity<ApiResponse<DentistDetailResponse>> createDentistAccount(
            @Valid @RequestBody DentistCreateRequest request){
        ApiResponse<DentistDetailResponse> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Create account successfully!",
                dentistService.createDentist(request));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Approve dentist account", description = "Approve dentist account by set the status as ACTIVE and " +
            "send a confirmation email to the dentist's Email with providing account username/pass. " +
            "Only System Admin can perform this request!")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/approval/{dentistId}")
    public ResponseEntity<ApiResponse<DentistDetailResponse>> approveDentistAccount(
             @PathVariable("dentistId") Long id, @RequestParam boolean isApproved){
        ApiResponse<DentistDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Approve dentist successfully!",
                dentistService.approveDentistAccount(id, isApproved));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get dentist list by branch", description = "If branchId is null, it will return all dentist account in the system," +
            " but if branchIdi is set it will return the list of that branch. " +
            "Anyone can perform this request!")
        @GetMapping("")
    public ResponseEntity<ApiResponse<Page<DentistListResponse>>> getDentistList(
            @RequestParam(required = false) Long branchId,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String dir,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String by)
    {
        ApiResponse<Page<DentistListResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get list dentist successfully!",
                dentistService.getDentistListByBranch(branchId, page, size, dir, by));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get pending dentist account list", description = "Get all pending dentist account for the approval account request. " +
            "Only System Admin can perform this request!")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<DentistListResponse>>> getPendingList(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size){
        ApiResponse<Page<DentistListResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get pending dentist list successfully!",
                dentistService.getPendingDentistList(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get dentist detail information", description = "Get dentist details information by dentistId. " +
            "Anyone can perform this request!")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DENTIST', 'OWNER', 'STAFF')")
    @GetMapping("/{dentistId}")
    public ResponseEntity<ApiResponse<DentistDetailResponse>> getDentistDetail(
            @PathVariable("dentistId") Long dentistId){
        ApiResponse<DentistDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get dentist detail successfully!",
                dentistService.getDentistDetail(dentistId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update dentist account", description = "Update dentist account information, " +
            " the status can only be updated between ACTIVE and INACTIVE. If status is PENDING, it will throw error. " +
            "Only System Admin, Dentist, Clinic Owner, Clinic Staff can perform this request!")
    @PreAuthorize("hasAnyRole('ADMIN', 'DENTIST', 'OWNER', 'STAFF')")
    @PutMapping()
    public ResponseEntity<ApiResponse<DentistDetailResponse>> updateDentist(
            @Valid @RequestBody DentistUpdateRequest request){
        ApiResponse<DentistDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Create account successfully!",
                dentistService.updateDentistDetails(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Remove dentist account (SOFT DELETE)", description = "Remove dentist account by change the status to INACTIVE. " +
            "Only System Admin, Clinic Owner, Clinic Staff can perform this request!")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'OWNER')")
    @DeleteMapping("/{dentistId}")
    public ResponseEntity<ApiResponse<DentistDetailResponse>> removeDentist(
            @PathVariable("dentistId") Long dentistId){
        ApiResponse<DentistDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Create account successfully!",
                dentistService.removeDentist(dentistId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
