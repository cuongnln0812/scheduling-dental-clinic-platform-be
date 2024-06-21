package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.WorkingHoursCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.WorkingHoursResponse;
import com.example.dentalclinicschedulingplatform.service.impl.WorkingHoursService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Tag(name = "Working Hour Controller")
@RequestMapping("/api/v1/working-hours")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WorkingHourseController {
    private final WorkingHoursService workingHourService;

    @Operation(summary = "Create new working hours for Clinic")
    @PreAuthorize("hasAnyRole('OWNER')")
    @PostMapping
    public ResponseEntity<ApiResponse<List<WorkingHoursResponse>>> createWorkingHours
            (@Valid @RequestBody List<WorkingHoursCreateRequest> request){
        List<WorkingHoursResponse> workingHoursResponseList = workingHourService.createWorkingHour(request);
        ApiResponse<List<WorkingHoursResponse>> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Created working hours successfully",
                workingHoursResponseList);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
