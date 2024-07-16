package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.OwnerViewResponse;
import com.example.dentalclinicschedulingplatform.service.IOwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Owner Controller")
@RequestMapping("api/v1/owners")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class OwnerController {

    private final IOwnerService ownerService;

    @Operation(
            summary = "Get owner detail information"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OwnerViewResponse>> getOwnerDetail(@PathVariable("id") Long id) {
        OwnerViewResponse owner = ownerService.getOwnerDetail(id);
        ApiResponse<OwnerViewResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get owner detail information successfully",
                owner);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Activate/ Deactivate Clinic owner"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/status/{id}")
    public ResponseEntity<ApiResponse<OwnerViewResponse>> activateDeactivateOwner(@PathVariable("id") Long id) {
        OwnerViewResponse owner = ownerService.activateDeactivateOwner(id);
        ApiResponse<OwnerViewResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                owner.getStatus().equals(ClinicStatus.ACTIVE) ? "Activate owner successfully" : "Deactivate owner successfully",
                owner);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
