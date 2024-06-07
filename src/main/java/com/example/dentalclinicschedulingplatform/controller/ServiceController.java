package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.CategoryCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.ServiceCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.CategoryViewResponse;
import com.example.dentalclinicschedulingplatform.payload.response.ServiceViewResponse;
import com.example.dentalclinicschedulingplatform.service.impl.AuthenticateService;
import com.example.dentalclinicschedulingplatform.service.impl.CategoryService;
import com.example.dentalclinicschedulingplatform.service.impl.DentalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/service")
@SecurityRequirement(name = "bearerAuth")
public class ServiceController {
    @Autowired
    private DentalService dentalService;

    @Autowired
    private AuthenticateService authenticationService;

    @PostMapping("/create")
    public ResponseEntity<ServiceViewResponse> createNewService(@Valid @RequestBody ServiceCreateRequest request){
        ServiceViewResponse newService = dentalService.createNewService(authenticationService.getUserInfo(), request);
        return ResponseEntity.ok(newService);
    }
}
