package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TreatmentOutcomeResponse {
    private Long id;
    private String diagnosis;
    private String treatmentPlan;
    private String prescription;
    private String recommendations;
    private String followUpDate;
    private String createdBy;
    private String modifiedBy;
    private Long appointmentId;
    private Long customerId;
}
