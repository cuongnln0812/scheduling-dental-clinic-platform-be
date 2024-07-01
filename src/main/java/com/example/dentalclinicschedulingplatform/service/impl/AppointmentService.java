package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.Appointment;
import com.example.dentalclinicschedulingplatform.entity.Customer;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.response.AppointmentViewListResponse;
import com.example.dentalclinicschedulingplatform.repository.AppoinmentRepository;
import com.example.dentalclinicschedulingplatform.repository.CustomerRepository;
import com.example.dentalclinicschedulingplatform.service.IAppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService implements IAppointmentService {

    private final CustomerRepository customerRepository;
    private final AppoinmentRepository appoinmentRepository;
    private final ModelMapper modelMapper;
    @Override
    public List<AppointmentViewListResponse> getCustomerAppointments(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));

        List<Appointment> appointments = appoinmentRepository.findAppointmentsByCustomerId(customer.getId());

        List<AppointmentViewListResponse> appointmentViewListResponses = new ArrayList<>();

        for (Appointment appointment: appointments) {
            appointmentViewListResponses.add(modelMapper.map(appointment, AppointmentViewListResponse.class));
        }

        return appointmentViewListResponses;
    }

    @Override
    public Appointment makeAppointment(Appointment appointment) {
        return null;
    }
}
