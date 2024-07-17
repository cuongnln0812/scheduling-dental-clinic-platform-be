package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.Appointment;
import com.example.dentalclinicschedulingplatform.entity.Customer;
import com.example.dentalclinicschedulingplatform.entity.TreatmentOutcome;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.TreatmentOutcomeRequest;
import com.example.dentalclinicschedulingplatform.payload.response.TreatmentOutcomeResponse;
import com.example.dentalclinicschedulingplatform.repository.AppointmentRepository;
import com.example.dentalclinicschedulingplatform.repository.CustomerRepository;
import com.example.dentalclinicschedulingplatform.repository.TreatmentOutcomeRepository;
import com.example.dentalclinicschedulingplatform.service.ITreatmentOutcomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentOutcomeService implements ITreatmentOutcomeService {

    private final TreatmentOutcomeRepository treatmentOutcomeRepository;
    private final CustomerRepository customerRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public TreatmentOutcomeResponse createTreatmentOutcome(TreatmentOutcomeRequest request) {
        TreatmentOutcome treatmentOutcome = new TreatmentOutcome();
        treatmentOutcome.setDiagnosis(request.getDiagnosis());
        treatmentOutcome.setTreatmentPlan(request.getTreatmentPlan());
        treatmentOutcome.setPrescription(request.getPrescription());
        treatmentOutcome.setRecommendations(request.getRecommendations());
        treatmentOutcome.setFollowUpDate(LocalDate.parse(request.getFollowUpDate()));

        Customer customer = customerRepository.findByUsername(request.getCustomerUsername())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));
        treatmentOutcome.setCustomer(customer);

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));
        treatmentOutcome.setAppointment(appointment);

        treatmentOutcome = treatmentOutcomeRepository.save(treatmentOutcome);

        return new TreatmentOutcomeResponse(treatmentOutcome.getId(), treatmentOutcome.getDiagnosis(), treatmentOutcome.getTreatmentPlan(), treatmentOutcome.getPrescription(), treatmentOutcome.getRecommendations(), treatmentOutcome.getFollowUpDate().toString(),
                treatmentOutcome.getCreatedBy(), treatmentOutcome.getModifiedBy(), appointment.getId(), customer.getId(), appointment.getCustomerName(), appointment.getDentist().getFullName(), appointment.getClinicBranch().getBranchName());
    }

    @Override
    public TreatmentOutcomeResponse updateTreatmentOutcome(Long id, TreatmentOutcomeRequest request) {
        TreatmentOutcome treatmentOutcome = treatmentOutcomeRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Treatment Outcome not found"));

        treatmentOutcome.setDiagnosis(request.getDiagnosis());
        treatmentOutcome.setTreatmentPlan(request.getTreatmentPlan());
        treatmentOutcome.setPrescription(request.getPrescription());
        treatmentOutcome.setRecommendations(request.getRecommendations());
        treatmentOutcome.setFollowUpDate(LocalDate.parse(request.getFollowUpDate()));

        Appointment currAppointment = appointmentRepository.findById(treatmentOutcome.getAppointment().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Customer currCustomer = customerRepository.findById(treatmentOutcome.getCustomer().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));

        treatmentOutcome = treatmentOutcomeRepository.save(treatmentOutcome);

        return new TreatmentOutcomeResponse(treatmentOutcome.getId(), treatmentOutcome.getDiagnosis(), treatmentOutcome.getTreatmentPlan(), treatmentOutcome.getPrescription(), treatmentOutcome.getRecommendations(), treatmentOutcome.getFollowUpDate().toString(),
                treatmentOutcome.getCreatedBy(), treatmentOutcome.getModifiedBy(), currAppointment.getId(), currCustomer.getId(), currAppointment.getCustomerName(), currAppointment.getDentist().getFullName(), currAppointment.getClinicBranch().getBranchName());
    }

    @Override
    public void removeTreatmentOutcome(Long id) {
        TreatmentOutcome treatmentOutcome = treatmentOutcomeRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Treatment Outcome not found"));
        treatmentOutcomeRepository.delete(treatmentOutcome);
    }

    @Override
    public List<TreatmentOutcomeResponse> viewTreatmentOutcomesByCustomer(String username) {

        List<TreatmentOutcomeResponse> treatmentOutcomeResponses = new ArrayList<>();

        Customer currCustomer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));

        List<TreatmentOutcome> treatmentOutcomes = treatmentOutcomeRepository.findByCustomerUsername(currCustomer.getUsername());

        for (TreatmentOutcome outcome : treatmentOutcomes) {

            Appointment currAppointment = appointmentRepository.findById(outcome.getAppointment().getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));

            treatmentOutcomeResponses.add(new TreatmentOutcomeResponse(outcome.getId(), outcome.getDiagnosis(), outcome.getTreatmentPlan(), outcome.getPrescription(), outcome.getRecommendations(), outcome.getFollowUpDate().toString(),
                    outcome.getCreatedBy(), outcome.getModifiedBy(), currAppointment.getId(), currCustomer.getId(), currAppointment.getCustomerName(), currAppointment.getDentist().getFullName(), currAppointment.getClinicBranch().getBranchName()));

        }

        return treatmentOutcomeResponses;
    }

    @Override
    public TreatmentOutcomeResponse viewTreatmentOutcomeByAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));
        TreatmentOutcome treatmentOutcome = treatmentOutcomeRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Treatment Outcome not found"));

        return new TreatmentOutcomeResponse(treatmentOutcome.getId(), treatmentOutcome.getDiagnosis(), treatmentOutcome.getTreatmentPlan(), treatmentOutcome.getPrescription(), treatmentOutcome.getRecommendations(), treatmentOutcome.getFollowUpDate().toString(),
                treatmentOutcome.getCreatedBy(), treatmentOutcome.getModifiedBy(), appointment.getId(), appointment.getCustomer().getId(), appointment.getCustomerName(), appointment.getDentist().getFullName(), appointment.getClinicBranch().getBranchName());
    }
}
