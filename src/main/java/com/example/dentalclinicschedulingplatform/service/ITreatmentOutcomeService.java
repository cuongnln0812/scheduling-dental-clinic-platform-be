package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.TreatmentOutcome;
import com.example.dentalclinicschedulingplatform.payload.request.TreatmentOutcomeRequest;
import com.example.dentalclinicschedulingplatform.payload.response.TreatmentOutcomeResponse;

import java.util.List;

public interface ITreatmentOutcomeService {
    TreatmentOutcomeResponse createTreatmentOutcome(TreatmentOutcomeRequest request);
    TreatmentOutcomeResponse updateTreatmentOutcome(Long id, TreatmentOutcomeRequest request);
    void removeTreatmentOutcome(Long id);
    List<TreatmentOutcomeResponse> viewTreatmentOutcomesByCustomer(String username);
    TreatmentOutcomeResponse viewTreatmentOutcomeByAppointment(Long appointmentId);
}
