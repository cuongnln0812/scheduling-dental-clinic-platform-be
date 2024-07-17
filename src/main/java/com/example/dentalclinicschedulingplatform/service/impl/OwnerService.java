package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.ClinicOwner;
import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import com.example.dentalclinicschedulingplatform.entity.UserType;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.request.OwnerRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.OwnerViewResponse;
import com.example.dentalclinicschedulingplatform.repository.OwnerRepository;
import com.example.dentalclinicschedulingplatform.service.IMailService;
import com.example.dentalclinicschedulingplatform.service.IOwnerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerService implements IOwnerService {

    private final PasswordEncoder passwordEncoder;
    private final OwnerRepository ownerRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ClinicOwner registerOwnerFromRequest(OwnerRegisterRequest request) {
        ClinicOwner owner = new ClinicOwner();
        if(ownerRepository.existsByEmail(request.getEmail()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already used!");
        owner.setFullName(request.getFullName());
        owner.setEmail(request.getEmail());
        owner.setPhone(request.getPhone());
        owner.setStatus(ClinicStatus.PENDING);
        return ownerRepository.save(owner);
    }

    @Override
    public ClinicOwner approveOwnerAccount(Long ownerId, String randomPassword) {
        ClinicOwner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", ownerId));
        owner.setStatus(ClinicStatus.ACTIVE);
        owner.setUsername("owner" + owner.getId());
        owner.setPassword(passwordEncoder.encode(randomPassword));
        return ownerRepository.save(owner);
    }

    @Override
    public OwnerViewResponse getOwnerDetail(Long id) {
        ClinicOwner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", id));
        return modelMapper.map(owner, OwnerViewResponse.class);
    }

    @Override
    public OwnerViewResponse activateDeactivateOwner(Long ownerId) {
        ClinicOwner currOwner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner not found"));

        if (currOwner.getStatus().equals(ClinicStatus.ACTIVE)) {
            currOwner.setStatus(ClinicStatus.INACTIVE);
        }else if (currOwner.getStatus().equals(ClinicStatus.INACTIVE)) {
            currOwner.setStatus(ClinicStatus.ACTIVE);
        }

        ownerRepository.save(currOwner);

        return modelMapper.map(currOwner, OwnerViewResponse.class);
    }
}
