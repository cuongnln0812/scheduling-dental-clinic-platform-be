package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.ServiceCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.ServiceUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CategoryViewResponse;
import com.example.dentalclinicschedulingplatform.payload.response.ServiceViewDetailsResponse;
import com.example.dentalclinicschedulingplatform.payload.response.ServiceViewListResponse;
import com.example.dentalclinicschedulingplatform.service.impl.AuthenticateService;
import com.example.dentalclinicschedulingplatform.service.impl.DentalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service")
@SecurityRequirement(name = "bearerAuth")
public class ServiceController {
    @Autowired
    private DentalService dentalService;

    @Autowired
    private AuthenticateService authenticationService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<ServiceViewListResponse>>> viewCategoryListByClinic
            (@RequestParam(required = false) Long clinicId,
             @RequestParam int page,
             @RequestParam int size){
        ApiResponse<List<ServiceViewListResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get categories successfully",
                dentalService.viewServicesByClinic(clinicId, page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

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
}
