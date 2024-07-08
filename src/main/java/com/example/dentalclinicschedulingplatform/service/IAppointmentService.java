package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.Appointment;
import com.example.dentalclinicschedulingplatform.payload.request.AppointmentCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.AppointmentUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CancelAppointmentRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IAppointmentService {
    Map<String, Object> getCustomerAppointments(UserInformationRes userInformationRes, int page, int size);
    List<AppointmentBranchResponse> getAppointmentsOfClinicBranch(UserInformationRes userInformationRes, int page, int size);
    AppointmentViewDetailsResponse makeAppointment(AppointmentCreateRequest appointment);
    AppointmentViewDetailsResponse viewDetailsAppointment(Long appointmentId);
    AppointmentViewDetailsResponse cancelAppointment(CancelAppointmentRequest request);
    AppointmentViewDetailsResponse updateAppointment(AppointmentUpdateRequest appointment);
    List<AppointmentDentistViewListResponse> getAppointmentsOfDentist(UserInformationRes userInformationRes, int page, int size, LocalDate startDate, LocalDate endDate);
}
