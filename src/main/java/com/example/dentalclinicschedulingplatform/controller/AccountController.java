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
    private final IOwnerService ownerService;

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
            summary = "Activate/ Deactivate Clinic owner"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/owner/activate-deactivate/{id}")
    public ResponseEntity<ApiResponse<OwnerViewResponse>> activateDeactivateOwner(@PathVariable("id") Long id) {
        OwnerViewResponse owner = ownerService.activateDeactivateOwner(id);
        ApiResponse<OwnerViewResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                owner.getStatus().equals(ClinicStatus.ACTIVE) ? "Activate owner successfully" : "Deactivate owner successfully",
                owner);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Activate/ Deactivate customer"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/customer/activate-deactivate/{id}")
    public ResponseEntity<ApiResponse<CustomerViewResponse>> activateDeactivateCustomer(@PathVariable("id") Long id) {
        CustomerViewResponse customer = accountService.activateDeactivateCustomer(id);
        ApiResponse<CustomerViewResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                customer.isStatus() ? "Activate customer successfully" : "Deactivate customer successfully",
                customer);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
