package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CustomerViewResponse;
import com.example.dentalclinicschedulingplatform.service.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Customer Controller")
@RequiredArgsConstructor
public class CustomerController {
    private final ICustomerService customerService;

    @Operation(
            summary = "Get customer detail information"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerViewResponse>> getCustomerDetail(@PathVariable("id") Long id) {
        CustomerViewResponse customer = customerService.getCustomerDetail(id);
        ApiResponse<CustomerViewResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get customer detail information successfully",
                customer);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Activate/ Deactivate customer"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/status/{id}")
    public ResponseEntity<ApiResponse<CustomerViewResponse>> activateDeactivateCustomer(@PathVariable("id") Long id) {
        CustomerViewResponse customer = customerService.activateDeactivateCustomer(id);
        ApiResponse<CustomerViewResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                customer.isStatus() ? "Activate customer successfully" : "Deactivate customer successfully",
                customer);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
