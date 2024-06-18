package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.ClinicRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ClinicRegisterResponse;

public interface IClinicService {
    ClinicRegisterResponse registerClinic(ClinicRegisterRequest request);
    void approveClinic(Long clinicId);

}
