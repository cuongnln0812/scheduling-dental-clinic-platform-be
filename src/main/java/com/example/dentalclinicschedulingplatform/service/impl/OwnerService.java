package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.ClinicOwner;
import com.example.dentalclinicschedulingplatform.entity.Status;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.OwnerRegisterRequest;
import com.example.dentalclinicschedulingplatform.repository.OwnerRepository;
import com.example.dentalclinicschedulingplatform.service.IOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerService implements IOwnerService {

    private final OwnerRepository ownerRepository;

    @Override
    @Transactional
    public ClinicOwner registerOwnerFromRequest(OwnerRegisterRequest request) {
        ClinicOwner owner = new ClinicOwner();
        if(ownerRepository.existsByEmail(request.getEmail()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already used!");
        owner.setFullName(request.getFullName());
        owner.setEmail(request.getEmail());
        owner.setPhone(request.getPhone());
        owner.setStatus(Status.PENDING);
        return ownerRepository.save(owner);
    }

    @Override
    public ClinicOwner approveOwnerAccount(Long ownerId) {
        return null;
    }
}
