package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.DentistCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.DentistUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.DentistDetailResponse;
import com.example.dentalclinicschedulingplatform.payload.response.DentistListResponse;
import com.example.dentalclinicschedulingplatform.service.IDentistService;
import com.example.dentalclinicschedulingplatform.utils.AppConstants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dentist")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class DentistController {

    private final IDentistService dentistService;

    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    @PostMapping()
    public ResponseEntity<ApiResponse<DentistDetailResponse>> createDentistAccount(
            @Valid @RequestBody DentistCreateRequest request){
        ApiResponse<DentistDetailResponse> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Create account successfully!",
                dentistService.createDentist(request));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'DENTIST', 'OWNER', 'STAFF')")
    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<DentistListResponse>>> getDentistList(
            @RequestParam(required = false) Long branchId,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size){
        ApiResponse<Page<DentistListResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get list dentist successfully!",
                dentistService.getDentistListByBranch(branchId, page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DENTIST', 'OWNER', 'STAFF')")
    @GetMapping("/{dentistId}")
    public ResponseEntity<ApiResponse<DentistDetailResponse>> getDentistList(
            @PathVariable("dentistId") Long dentistId){
        ApiResponse<DentistDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get dentist detail successfully!",
                dentistService.getDentistDetail(dentistId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('DENTIST', 'OWNER', 'STAFF')")
    @PutMapping()
    public ResponseEntity<ApiResponse<DentistDetailResponse>> updateDentist(
            @Valid @RequestBody DentistUpdateRequest request){
        ApiResponse<DentistDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Create account successfully!",
                dentistService.updateDentistDetails(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('OWNER')")
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
