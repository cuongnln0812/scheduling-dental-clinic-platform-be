package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TreatmentOutcomeRequest {
    @NotBlank(message = "Diagnosis cannot be blank")
    private String diagnosis;

    @NotBlank(message = "Treatment plan cannot be blank")
    private String treatmentPlan;

    @NotBlank(message = "Prescription cannot be blank")
    private String prescription;

    @NotBlank(message = "Recommendations cannot be blank")
    private String recommendations;

    @NotBlank(message = "Follow-up date cannot be blank")
    private String followUpDate;

    @NotNull(message = "Appointment ID cannot be null")
    private Long appointmentId;
}
