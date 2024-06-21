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
            summary = "View slot by clinicId"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkingHoursDetailsResponse>>> viewSlotListByCategory
            (@RequestParam(required = true) Long clinicId){
        ApiResponse<List<WorkingHoursDetailsResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get slots successfully",
                slotService.viewSlotListByClinicId(clinicId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
