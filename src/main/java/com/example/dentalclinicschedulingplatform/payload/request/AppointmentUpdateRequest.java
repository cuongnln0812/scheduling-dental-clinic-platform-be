package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentUpdateRequest {

    @NotNull(message = "Appointment Id is required")
    public Long appointmentId;

    @NotBlank(message = "Customer name is required")
    public String customerName;

    @NotBlank(message = "Customer address is required")
    private String customerAddress;

    @NotBlank(message = "Customer phone is required")
    private String customerPhone;

    @NotNull(message = "Customer date of birth is required")
    private LocalDate customerDob;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email should be valid")
    private String customerEmail;

    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;

    @NotNull(message = "Slot ID is required")
    private Long slotId;

    @NotNull(message = "Dentist ID is required")
    private Long dentistId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;
}
