package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.payload.response.AccountListResponse;
import com.example.dentalclinicschedulingplatform.repository.*;
import com.example.dentalclinicschedulingplatform.service.IAccountService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;
    private final DentistRepository dentistRepository;
    private final StaffRepository staffRepository;
    private final OwnerRepository ownerRepository;
    private final SystemAdminRepository adminRepository;

    @Override
    public Page<AccountListResponse> getAllAccount(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Customer> customerPage = customerRepository.findAll(pageable);
        Page<AccountListResponse> customerResponses = customerPage.map(user -> {
                AccountListResponse res = modelMapper.map(user, AccountListResponse.class);
                res.setRoleName("CUSTOMER");
                res.setStatus(user.isStatus() ? "ACTIVE" : "INACTIVE");
                return res;
        });

        Page<Dentist> dentistPage = dentistRepository.findAllActiveAndInactive(pageable);
        Page<AccountListResponse> dentistResponses = dentistPage.map(user -> {
                AccountListResponse res = modelMapper.map(user, AccountListResponse.class);
                res.setRoleName("DENTIST");
                res.setStatus(user.getStatus().toString());
                return res;
        });

        Page<ClinicStaff> staffPage = staffRepository.findAllActiveAndInactive(pageable);
        Page<AccountListResponse> staffResponse = staffPage.map(user -> {
                AccountListResponse res = modelMapper.map(user, AccountListResponse.class);
                res.setRoleName("STAFF");
                res.setStatus(user.getStatus().toString());
                return res;
        });

        Page<ClinicOwner> ownerPage = ownerRepository.findAllActiveAndInactive(pageable);
        Page<AccountListResponse> ownerResponses = ownerPage.map(user -> {
            AccountListResponse res = modelMapper.map(user, AccountListResponse.class);
            res.setRoleName("OWNER");
            res.setStatus(user.getStatus().toString());
            return res;
        });

        Page<SystemAdmin> adminPage = adminRepository.findAll(pageable);
        Page<AccountListResponse> adminResponse = adminPage.map(user -> {
            AccountListResponse res = modelMapper.map(user, AccountListResponse.class);
            res.setId(user.getAdminId());
            res.setRoleName("ADMIN");
            res.setStatus(user.isStatus() ? "ACTIVE" : "INACTIVE");
            return res;
        });

        return PageableExecutionUtils.getPage(
                Stream.of(customerResponses, dentistResponses, staffResponse, ownerResponses, adminResponse)
                        .flatMap(pageContent -> pageContent.getContent().stream())
                        .collect(Collectors.toList()),
                pageable,
                () -> {
                    long totalElements = customerResponses.getTotalElements() +
                            dentistResponses.getTotalElements() +
                            staffResponse.getTotalElements() +
                            ownerResponses.getTotalElements() +
                            adminResponse.getTotalElements();
                    return totalElements;
                }
        );
    }
}
