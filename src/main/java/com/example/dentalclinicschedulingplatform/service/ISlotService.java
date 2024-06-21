package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.response.WorkingHoursDetailsResponse;

import java.util.List;

public interface ISlotService {
    void generateSlotForDentalClinic(Long workingHourId);

    List<WorkingHoursDetailsResponse> viewSlotListByClinicId(Long clinicId);
}
