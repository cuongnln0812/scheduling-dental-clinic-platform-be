package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.AppointmentViewListResponse;
import com.example.dentalclinicschedulingplatform.service.impl.AppointmentService;
import com.example.dentalclinicschedulingplatform.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointment")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Appointment Controller")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(
            summary = "View appointment of customer"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<AppointmentViewListResponse>>> getCustomerAppointments
            (@RequestParam(required = false) Long customerId){
        ApiResponse<List<AppointmentViewListResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get appointments successfully",
                appointmentService.getCustomerAppointments(customerId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
