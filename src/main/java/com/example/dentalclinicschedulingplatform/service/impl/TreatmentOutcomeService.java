package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.Appointment;
import com.example.dentalclinicschedulingplatform.entity.Customer;
import com.example.dentalclinicschedulingplatform.entity.TreatmentOutcome;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.TreatmentOutcomeRequest;
import com.example.dentalclinicschedulingplatform.payload.response.TreatmentOutcomeResponse;
import com.example.dentalclinicschedulingplatform.repository.AppoinmentRepository;
import com.example.dentalclinicschedulingplatform.repository.CustomerRepository;
import com.example.dentalclinicschedulingplatform.repository.TreatmentOutcomeRepository;
import com.example.dentalclinicschedulingplatform.service.ITreatmentOutcomeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TreatmentOutcomeService implements ITreatmentOutcomeService {

    private final TreatmentOutcomeRepository treatmentOutcomeRepository;
    private final CustomerRepository customerRepository;
    private final AppoinmentRepository appointmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public TreatmentOutcomeResponse createTreatmentOutcome(TreatmentOutcomeRequest request) {
        TreatmentOutcome treatmentOutcome = new TreatmentOutcome();
        treatmentOutcome.setDiagnosis(request.getDiagnosis());
        treatmentOutcome.setTreatmentPlan(request.getTreatmentPlan());
        treatmentOutcome.setPrescription(request.getPrescription());
        treatmentOutcome.setRecommendations(request.getRecommendations());
        treatmentOutcome.setFollowUpDate(LocalDate.parse(request.getFollowUpDate()));

        Customer customer = customerRepository.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));
        treatmentOutcome.setCustomer(customer);

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));
        treatmentOutcome.setAppointment(appointment);

        treatmentOutcome = treatmentOutcomeRepository.save(treatmentOutcome);
        return modelMapper.map(treatmentOutcome, TreatmentOutcomeResponse.class);
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

        treatmentOutcome = treatmentOutcomeRepository.save(treatmentOutcome);
        return modelMapper.map(treatmentOutcome, TreatmentOutcomeResponse.class);
    }

    @Override
    public void removeTreatmentOutcome(Long id) {
        TreatmentOutcome treatmentOutcome = treatmentOutcomeRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Treatment Outcome not found"));
        treatmentOutcomeRepository.delete(treatmentOutcome);
    }

    @Override
    public List<TreatmentOutcomeResponse> viewTreatmentOutcomesByCustomer(String username) {
        List<TreatmentOutcome> treatmentOutcomes = treatmentOutcomeRepository.findByCustomerUsername(username);
        return treatmentOutcomes.stream()
                .map(treatmentOutcome -> modelMapper.map(treatmentOutcome, TreatmentOutcomeResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public TreatmentOutcomeResponse viewTreatmentOutcomeByAppointment(Long appointmentId) {
        TreatmentOutcome treatmentOutcome = treatmentOutcomeRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Treatment Outcome not found"));
        return modelMapper.map(treatmentOutcome, TreatmentOutcomeResponse.class);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
