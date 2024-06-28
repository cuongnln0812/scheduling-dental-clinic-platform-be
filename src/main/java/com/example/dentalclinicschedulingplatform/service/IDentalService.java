package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.Status;
import com.example.dentalclinicschedulingplatform.payload.request.ServiceCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.ServiceUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.CategoryViewResponse;
import com.example.dentalclinicschedulingplatform.payload.response.ServiceViewDetailsResponse;
import com.example.dentalclinicschedulingplatform.payload.response.ServiceViewListResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;

import java.util.List;

public interface IDentalService {
    List<ServiceViewDetailsResponse> viewServicesByCategoryId(Long categoryId);
    List<ServiceViewListResponse> viewServicesByClinic(Long clinicId, int page, int size);
    ServiceViewDetailsResponse createNewService(UserInformationRes userInformationRes, ServiceCreateRequest request);
    ServiceViewDetailsResponse updateService(UserInformationRes userInformationRes, ServiceUpdateRequest request);
    ServiceViewDetailsResponse deleteService(UserInformationRes userInformation, Long serviceId);
    ServiceViewDetailsResponse changeServiceStatus(UserInformationRes userInformation, Long serviceId, Status status);
    ServiceViewDetailsResponse viewDetailsService(UserInformationRes userInformationRes, Long serviceId);
//    List<ServiceViewListResponse>
}
