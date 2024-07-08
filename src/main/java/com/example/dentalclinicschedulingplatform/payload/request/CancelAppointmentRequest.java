package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CancelAppointmentRequest {

    @NotNull(message = "Appointment id can not be null")
    public Long appointmentId;

    @NotNull(message = "Reason for cancelling can not be null")
    public String cancelReason;
}
