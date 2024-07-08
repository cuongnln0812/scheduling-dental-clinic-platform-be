package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentBranchResponse {
    public Long appointmentId;

    public String appointmentStatus;

    public Long customerId;

    public String customerName;

    public String service;

    public String dentistName;

    public LocalDateTime createdDate;
}
