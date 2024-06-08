package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.CategoryCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.ServiceCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.ServiceUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CategoryViewResponse;
import com.example.dentalclinicschedulingplatform.payload.response.ServiceViewResponse;
import com.example.dentalclinicschedulingplatform.service.impl.AuthenticateService;
import com.example.dentalclinicschedulingplatform.service.impl.CategoryService;
import com.example.dentalclinicschedulingplatform.service.impl.DentalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service")
@SecurityRequirement(name = "bearerAuth")
public class ServiceController {
    @Autowired
    private DentalService dentalService;

    @Autowired
    private AuthenticateService authenticationService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ServiceViewResponse>> createNewService(@Valid @RequestBody ServiceCreateRequest request){
        ServiceViewResponse newService = dentalService.createNewService(authenticationService.getUserInfo(), request);
        ApiResponse<ServiceViewResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Created service successfully",
                newService);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<ServiceViewResponse>> updateService(@Valid @RequestBody ServiceUpdateRequest request){
        ServiceViewResponse updateService = dentalService.updateService(authenticationService.getUserInfo(), request);
        ApiResponse<ServiceViewResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Updated service successfully",
                updateService);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
