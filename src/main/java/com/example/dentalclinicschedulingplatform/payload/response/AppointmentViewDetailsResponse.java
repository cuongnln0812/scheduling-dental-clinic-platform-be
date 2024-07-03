package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentViewDetailsResponse {

    public Long appointmentId;

    public String customerName;

    private String customerAddress;

    private String customerPhone;

    private LocalDate dob;

    private int customerAge;

    private String customerEmail;

    private LocalDate appointmentDate;

    private int duration;

    private SlotDetailsResponse slot;

    private BranchSummaryResponse clinicBranch;

    private DentistViewListResponse dentist;

    private ServiceViewListResponse service;
}
