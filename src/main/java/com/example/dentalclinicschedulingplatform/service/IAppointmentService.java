package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.Appointment;
import com.example.dentalclinicschedulingplatform.payload.request.AppointmentCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.AppointmentViewDetailsResponse;
import com.example.dentalclinicschedulingplatform.payload.response.AppointmentViewListResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;

import java.util.List;
import java.util.Map;

public interface IAppointmentService {
    Map<String, Object> getCustomerAppointments(UserInformationRes userInformationRes, int page, int size);
    Map<String, Object> getAppointments(UserInformationRes userInformationRes, int page, int size);
    AppointmentViewDetailsResponse makeAppointment(AppointmentCreateRequest appointment);
    AppointmentViewDetailsResponse viewDetailsAppointment(Long appointmentId);
    AppointmentViewDetailsResponse cancelAppointment(Long appointmentId);
}
