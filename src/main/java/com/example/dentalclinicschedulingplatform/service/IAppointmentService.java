package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.Appointment;
import com.example.dentalclinicschedulingplatform.payload.response.AppointmentViewListResponse;

import java.util.List;

public interface IAppointmentService {
    List<AppointmentViewListResponse> getCustomerAppointments(Long customerId);

    Appointment makeAppointment(Appointment appointment);
}
