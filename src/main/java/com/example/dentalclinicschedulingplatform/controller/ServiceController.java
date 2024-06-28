package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.ServiceChangeStatusRequest;
import com.example.dentalclinicschedulingplatform.payload.request.ServiceCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.ServiceUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CategoryViewResponse;
import com.example.dentalclinicschedulingplatform.payload.response.ServiceViewDetailsResponse;
import com.example.dentalclinicschedulingplatform.payload.response.ServiceViewListResponse;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import com.example.dentalclinicschedulingplatform.service.IDentalService;
import com.example.dentalclinicschedulingplatform.service.impl.AuthenticateService;
import com.example.dentalclinicschedulingplatform.service.impl.DentalService;
import com.example.dentalclinicschedulingplatform.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Service Controller")
public class ServiceController {

    private final IDentalService dentalService;
    private final IAuthenticateService authenticationService;

    @Operation(
            summary = "View all services (clinicId is null) / View services by clinic"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceViewListResponse>>> viewCategoryListByClinic
            (@RequestParam(required = false) Long clinicId,
             @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
             @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size){
        ApiResponse<List<ServiceViewListResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get categories successfully",
                dentalService.viewServicesByClinic(clinicId, page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Create service"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ServiceViewDetailsResponse>> createNewService
            (@Valid @RequestBody ServiceCreateRequest request){
        ServiceViewDetailsResponse newService = dentalService.createNewService(authenticationService.getUserInfo(), request);
        ApiResponse<ServiceViewDetailsResponse> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Created service successfully",
                newService);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update service"
    )
    @PutMapping
    public ResponseEntity<ApiResponse<ServiceViewDetailsResponse>> updateService
            (@Valid @RequestBody ServiceUpdateRequest request){
        ServiceViewDetailsResponse updateService = dentalService.updateService(authenticationService.getUserInfo(), request);
        ApiResponse<ServiceViewDetailsResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Updated service successfully",
                updateService);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete service by Id"
    )
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<ApiResponse<ServiceViewDetailsResponse>> deleteService
            (@PathVariable("serviceId") Long serviceId){
        ServiceViewDetailsResponse deletedService = dentalService.deleteService(authenticationService.getUserInfo(), serviceId);
        ApiResponse<ServiceViewDetailsResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Updated service successfully",
                deletedService);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Change status of service"
    )
    @PutMapping("/change-status")
    public ResponseEntity<ApiResponse<ServiceViewDetailsResponse>> changeServiceStatus
            (@RequestBody ServiceChangeStatusRequest request){
        ServiceViewDetailsResponse currService = dentalService.changeServiceStatus(authenticationService.getUserInfo(), request.getServiceId(), request.getServiceStatus());
        ApiResponse<ServiceViewDetailsResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Change service status successfully",
                currService);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "View details service by serviceId"
    )
    @PreAuthorize("hasAnyRole('OWNER')")
    @GetMapping("/{serviceId}")
    public ResponseEntity<ApiResponse<ServiceViewDetailsResponse>> viewDetailsService(@PathVariable("serviceId") Long serviceId){
        ServiceViewDetailsResponse category = dentalService.viewDetailsService(authenticationService.getUserInfo(), serviceId);
        ApiResponse<ServiceViewDetailsResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "View details service successfully",
                category);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
