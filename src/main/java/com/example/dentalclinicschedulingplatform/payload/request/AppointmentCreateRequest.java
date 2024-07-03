package com.example.dentalclinicschedulingplatform.payload.request;

import com.example.dentalclinicschedulingplatform.payload.response.SlotDetailsResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentCreateRequest {
    public String customerName;

    private String customerAddress;

    private String customerPhone;

    private int customerDob;

    private String customerEmail;

    private LocalDate appointmentDate;

    private Long slotId;

    private Long clinicBranchId;

    private Long dentistId;

    private Long serviceId;
}
