package com.example.dentalclinicschedulingplatform.controller;


import com.example.dentalclinicschedulingplatform.payload.request.BranchCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.BranchUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.service.IBranchService;
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

@RestController
@Slf4j
@Tag(name = "Branch Controller")
@RequestMapping("/api/v1/branch")
@Validated
@Valid
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BranchController {
    private final AuthenticateService authenticateService;
    private final IBranchService branchService;

    @Operation(
            summary = "View detail branch"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'STAFF', 'CUSTOMER', 'DENTIST')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchDetailResponse>> viewBranch(@PathVariable("id") Long id) {
        log.info("Has request with data: {}", id.toString());
        ApiResponse<BranchDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "View branch successfully",
                branchService.viewBranch(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Create branch"
    )
    @PreAuthorize("hasAnyRole('OWNER')")
    @PostMapping()
    public ResponseEntity<ApiResponse<BranchDetailResponse>> createBranch(@Valid @RequestBody BranchCreateRequest request) {
        log.info("Has request with data: {}", request.toString());
        BranchDetailResponse newBranch = branchService.createBranch(request);
        ApiResponse<BranchDetailResponse> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Create branch successfully",
                newBranch);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Approve for new branch"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/approval/{branchId}")
    public ResponseEntity<ApiResponse<BranchDetailResponse>> approveBranch(
            @PathVariable("branchId") Long id, @RequestParam boolean isApproved){
        ApiResponse<BranchDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Approve clinic branch successfully!",
                branchService.approveNewBranch(id, isApproved));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get pending branch list"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<BranchSummaryResponse>>> getPendingBranchList(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size){
        ApiResponse<Page<BranchSummaryResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get pending branches list successfully!",
                branchService.getBranchPendingList(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get pending branch detail"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/pending/{branchId}")
    public ResponseEntity<ApiResponse<BranchSummaryResponse>> getPendingBranchDetail(
            @PathVariable("branchId") Long id)
    {
        ApiResponse<BranchSummaryResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get pending branch detail successfully!",
                branchService.getBranchPendingDetail(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Update branch"
    )
    @PreAuthorize("hasAnyRole('OWNER')")
    @PutMapping()
    public ResponseEntity<ApiResponse<BranchSummaryResponse>> updateBranch(@Valid @RequestBody BranchUpdateRequest request)
    {
        ApiResponse<BranchSummaryResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Update branch successfully!",
                branchService.updateBranch(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete branch"
    )
    @PreAuthorize("hasAnyRole('OWNER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchSummaryResponse>> deleteStaff(@PathVariable("id") Long id) {
        log.info("Has request with data: {}", id.toString());
        ApiResponse<BranchSummaryResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Delete branch successfully",
                branchService.deleteBranch(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
