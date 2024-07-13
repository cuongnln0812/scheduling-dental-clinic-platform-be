package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private String customerName;
    private String dentistName;
    private String clinicBranchName;
}
