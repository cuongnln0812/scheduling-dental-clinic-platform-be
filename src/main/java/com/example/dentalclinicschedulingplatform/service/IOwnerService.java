package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.ClinicOwner;
import com.example.dentalclinicschedulingplatform.payload.request.OwnerRegisterRequest;

public interface IOwnerService {
    ClinicOwner registerOwnerFromRequest(OwnerRegisterRequest request);
    ClinicOwner approveOwnerAccount(Long ownerId, String password);
}
