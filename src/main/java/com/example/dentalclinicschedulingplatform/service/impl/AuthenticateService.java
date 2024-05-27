package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.UserType;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.AuthenticationRequest;
import com.example.dentalclinicschedulingplatform.payload.response.AuthenticationResponse;
import com.example.dentalclinicschedulingplatform.repository.CustomerRepository;
import com.example.dentalclinicschedulingplatform.repository.DentistRepository;
import com.example.dentalclinicschedulingplatform.repository.OwnerRepository;
import com.example.dentalclinicschedulingplatform.repository.StaffRepository;
import com.example.dentalclinicschedulingplatform.security.CustomUserDetailsService;
import com.example.dentalclinicschedulingplatform.security.JwtService;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

record ClinicAccount(UserDetails object, UserType userType){}

@Service
public class AuthenticateService implements IAuthenticateService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomerRepository customerRepository;
    private final DentistRepository dentistRepository;
    private final StaffRepository staffRepository;
    private final OwnerRepository ownerRepository;

    public AuthenticateService(JwtService jwtService, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, CustomerRepository customerRepository, DentistRepository dentistRepository, StaffRepository staffRepository, OwnerRepository ownerRepository) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.customerRepository = customerRepository;
        this.dentistRepository = dentistRepository;
        this.staffRepository = staffRepository;
        this.ownerRepository = ownerRepository;
    }

    public AuthenticationResponse authenticateCustomerAccount(AuthenticationRequest request) {
        customUserDetailsService.setUserType(UserType.CUSTOMER);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        var cus = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer cannot found!"));
        if(!cus.isStatus()) throw new ApiException(HttpStatus.FORBIDDEN, "Customer is forbidden!");
        var jwtToken = jwtService.generateToken(cus, UserType.CUSTOMER.toString());
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(jwtToken);
        return authenticationResponse;
    }

    public AuthenticationResponse authenticateClinicAccount(AuthenticationRequest request) {
        ClinicAccount acc = determineClinicAcc(request.getEmail());
        customUserDetailsService.setUserType(acc.userType());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        var jwtToken = jwtService.generateToken(acc.object(), UserType.DENTIST.toString());

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(jwtToken);
        return authenticationResponse;
    }

    private ClinicAccount determineClinicAcc(String email) {
        if (dentistRepository.existsByEmail(email)) {
            var dentist = dentistRepository.findByEmail(email)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dentist cannot found!"));
            if(!dentist.isStatus()) throw new ApiException(HttpStatus.FORBIDDEN, "Dentist is forbidden!");
            return new ClinicAccount(dentist, UserType.DENTIST);
        }else if (staffRepository.existsByEmail(email)) {
            var staff = staffRepository.findByEmail(email)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Staff cannot found!"));
            if(!staff.isStatus()) throw new ApiException(HttpStatus.FORBIDDEN, "Staff is forbidden!");
            return new ClinicAccount(staff, UserType.STAFF);
        }else if (ownerRepository.existsByEmail(email)) {
            var owner = ownerRepository.findByEmail(email)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner cannot found!"));
            if(!owner.isStatus()) throw new ApiException(HttpStatus.FORBIDDEN, "Owner is forbidden!");
            return new ClinicAccount(owner, UserType.OWNER);
        }
        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic account not found with email: " + email);
    }
}
