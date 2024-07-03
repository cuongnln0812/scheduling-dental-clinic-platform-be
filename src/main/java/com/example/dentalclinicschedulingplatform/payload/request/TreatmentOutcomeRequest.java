package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TreatmentOutcomeRequest {
    @NotBlank
    private String diagnosis;
    @NotBlank
    private String treatmentPlan;
    @NotBlank
    private String prescription;
    @NotBlank
    private String recommendations;
    @NotBlank
    private String followUpDate;
    @NotNull
    private Long appointmentId;
}
