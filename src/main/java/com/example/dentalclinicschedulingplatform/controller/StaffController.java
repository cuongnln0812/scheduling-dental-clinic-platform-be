package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.CreateStaffRequest;
import com.example.dentalclinicschedulingplatform.payload.response.StaffResponse;
import com.example.dentalclinicschedulingplatform.service.IStaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Slf4j
@Tag(name = "Staff Controller")
@RequestMapping("/staff")
@Validated
@Valid
@RequiredArgsConstructor
public class StaffController {

    @Autowired
    private IStaffService iStaffService;

    @Operation(
            summary = "Create"
    )
    @PostMapping("/api/v1/create")
    public ResponseEntity<StaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        log.info("Has request with data{}", request.toString());
        ResponseEntity<StaffResponse> staffResponse = iStaffService.createStaff(request);
        return staffResponse;
    }

}
