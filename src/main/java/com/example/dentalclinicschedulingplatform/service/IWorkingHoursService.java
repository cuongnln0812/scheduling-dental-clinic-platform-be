package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.WorkingHoursCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.WorkingHoursUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.WorkingHoursResponse;

import java.util.List;

public interface IWorkingHoursService {
    List<WorkingHoursResponse> createWorkingHour(List<WorkingHoursCreateRequest> workingHours);

    WorkingHoursResponse updateWorkingHour(WorkingHoursUpdateRequest workingHours);
}
