package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.ServiceCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ServiceViewResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;

import java.util.List;

public interface IDentalService {
    List<ServiceViewResponse> viewServicesByCategoryId(Long categoryId);
    List<ServiceViewResponse> viewServicesByClinicId(Long clinicId);
    ServiceViewResponse createNewService(UserInformationRes userInformationRes, ServiceCreateRequest request);
}
