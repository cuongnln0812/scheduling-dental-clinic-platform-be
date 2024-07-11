package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.AppointmentCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.AppointmentUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CancelAppointmentRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.service.impl.AppointmentService;
import com.example.dentalclinicschedulingplatform.service.impl.AuthenticateService;
import com.example.dentalclinicschedulingplatform.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
            summary = "View appointments for clinic branch"
    )
    @GetMapping("/branch")
    public ResponseEntity<ApiResponse<List<AppointmentBranchResponse>>> getAppointmentsForClinicBranch
            (@RequestParam (defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
             @RequestParam (defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size){
        ApiResponse<List<AppointmentBranchResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get appointments successfully",
                appointmentService.getAppointmentsOfClinicBranch(authenticateService.getUserInfo(), page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Make appointments"
    )
    @PostMapping()
    public ResponseEntity<ApiResponse<AppointmentViewDetailsResponse>> makeAppointment
            (@Valid @RequestBody AppointmentCreateRequest request){
        ApiResponse<AppointmentViewDetailsResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get appointments successfully",
                appointmentService.makeAppointment(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Cancel appointment"
    )
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<AppointmentViewDetailsResponse>> cancelAppointment
            (@Valid @RequestBody CancelAppointmentRequest request){
        ApiResponse<AppointmentViewDetailsResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Cancel appointments successfully",
                appointmentService.cancelAppointment(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Update appointment"
    )
    @PutMapping()
    public ResponseEntity<ApiResponse<AppointmentViewDetailsResponse>> updateAppointment
            (@Valid @RequestBody AppointmentUpdateRequest appointment){
        ApiResponse<AppointmentViewDetailsResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Update appointments successfully",
                appointmentService.updateAppointment(appointment));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "View appointments for clinic dentist"
    )
    @GetMapping("/dentist")
    public ResponseEntity<ApiResponse<List<AppointmentDentistViewListResponse>>> getAppointmentsForDentist
            (@RequestParam (defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
             @RequestParam (defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size,
             @RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        ApiResponse<List<AppointmentDentistViewListResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get appointments successfully",
                appointmentService.getAppointmentsOfDentist(authenticateService.getUserInfo(), page, size, startDate, endDate));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
