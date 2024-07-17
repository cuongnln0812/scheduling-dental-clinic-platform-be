package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.service.IAccountService;
import com.example.dentalclinicschedulingplatform.service.IOwnerService;
import com.example.dentalclinicschedulingplatform.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Account Controller")
public class AccountController {

    private final IAccountService accountService;

    @Operation(
            summary = "Get all account in the system", description = "Only System Admin can perform this!"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<AccountListResponse>>> getAllAccount(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size)
    {
        ApiResponse<Page<AccountListResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get all account successfully",
                accountService.getAllAccount(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Owner dashboard response", description = "Only Owner can perform this!"
    )
    @PreAuthorize("hasRole('Owner')")
    @GetMapping("/owner/dashboard")
    public ResponseEntity<ApiResponse<OwnerDashboardResponse>> getOwnerDashboardRes()
    {
        ApiResponse<OwnerDashboardResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get owner dashboard data successfully",
                accountService.getOwnerDashboardStatistics());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Admin dashboard response", description = "Only System Admin can perform this!"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getAdminDashboardRes()
    {
        ApiResponse<AdminDashboardResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get admin dashboard data successfully",
                accountService.getAdminDashboardStatistics());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
