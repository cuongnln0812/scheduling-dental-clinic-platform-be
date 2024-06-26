package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.WorkingHoursDetailsResponse;
import com.example.dentalclinicschedulingplatform.service.impl.SlotService;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/slot")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Slot Controller")
public class SlotController {
    private final SlotService slotService;

    @Operation(
            summary = "View slots of clinic branch"
    )
    @GetMapping()
    public ResponseEntity<ApiResponse<List<WorkingHoursDetailsResponse>>> viewSlotListByClinic
            (@RequestParam(required = true) Long clinicBranchId){
        ApiResponse<List<WorkingHoursDetailsResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get slots successfully",
                slotService.viewSlotListByClinicBranch(clinicBranchId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "View available slots by start date and end date of clinic branch"
    )
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<WorkingHoursDetailsResponse>>> viewAvailableSlots
            (@RequestParam(required = true) Long clinicBranchId,
             @RequestParam(required = true) LocalDate startDate,
             @RequestParam(required = true) LocalDate endDate){
        ApiResponse<List<WorkingHoursDetailsResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get slots successfully",
                slotService.viewAvailableSlotsByClinicBranch(startDate, endDate, clinicBranchId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "View available slots by date of clinic branch"
    )
    @GetMapping("/available-by-date")
    public ResponseEntity<ApiResponse<WorkingHoursDetailsResponse>> viewAvailableSlotsByDate
            (@RequestParam(required = true) Long clinicBranchId,
             @RequestParam(required = true) LocalDate date){
        ApiResponse<WorkingHoursDetailsResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get slots successfully",
                slotService.viewAvailableSlotsByDateByClinicBranch(date, clinicBranchId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
