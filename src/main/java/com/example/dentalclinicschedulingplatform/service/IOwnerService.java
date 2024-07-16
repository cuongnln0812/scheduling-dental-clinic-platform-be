package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.ClinicOwner;
import com.example.dentalclinicschedulingplatform.payload.request.OwnerRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.OwnerViewResponse;

public interface IOwnerService {
    ClinicOwner registerOwnerFromRequest(OwnerRegisterRequest request);
    ClinicOwner approveOwnerAccount(Long ownerId, String password);
    OwnerViewResponse getOwnerDetail(Long id);
    OwnerViewResponse activateDeactivateOwner(Long ownerId);
}
