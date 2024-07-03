package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.AppointmentViewDetailsResponse;
import com.example.dentalclinicschedulingplatform.payload.response.AppointmentViewListResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CategoryViewResponse;
import com.example.dentalclinicschedulingplatform.service.impl.AppointmentService;
import com.example.dentalclinicschedulingplatform.service.impl.AuthenticateService;
import com.example.dentalclinicschedulingplatform.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    private final AuthenticateService authenticateService;

    @Operation(
            summary = "View appointment of customer"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCustomerAppointments
            (@RequestParam (defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
             @RequestParam (defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size){
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get customer appointments successfully",
                appointmentService.getCustomerAppointments(authenticateService.getUserInfo(), page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "View details appointment"
    )
    @GetMapping("/{appointmentId}")
    public ResponseEntity<ApiResponse<AppointmentViewDetailsResponse>> viewDetailsAppointment(@PathVariable("appointmentId") Long appointmentId){
        AppointmentViewDetailsResponse appointment = appointmentService.viewDetailsAppointment(appointmentId);
        ApiResponse<AppointmentViewDetailsResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "View details category successfully",
                appointment);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "View appointments for staff/ dentist"
    )
    @GetMapping("/manage")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAppointments
            (@RequestParam (defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
             @RequestParam (defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size){
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get appointments successfully",
                appointmentService.getAppointments(authenticateService.getUserInfo(), page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
