package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDentistResponse {

    public Long appointmentId;

    public String appointmentStatus;

    public LocalDate appointmentDate;

    public SlotDetailsResponse slot;

    public Long customerId;

    public String customerName;

    public String service;
}
