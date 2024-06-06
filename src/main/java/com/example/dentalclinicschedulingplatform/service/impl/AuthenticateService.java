package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.Customer;
import com.example.dentalclinicschedulingplatform.entity.Status;
import com.example.dentalclinicschedulingplatform.entity.UserType;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.request.AuthenticationRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CustomerRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.AuthenticationResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CustomerRegisterResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

record ClinicAccount(UserDetails object, UserType userType){}

@Service
public class AuthenticateService implements IAuthenticateService {
    private final JwtService jwtService;
    private final PasswordEncoder  passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomerRepository customerRepository;
    private final DentistRepository dentistRepository;
    private final StaffRepository staffRepository;
    private final OwnerRepository ownerRepository;

    public AuthenticateService(JwtService jwtService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, CustomerRepository customerRepository, DentistRepository dentistRepository, StaffRepository staffRepository, OwnerRepository ownerRepository) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
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
                        request.getUsernameOrEmail(),
                        request.getPassword()));
        var cus = customerRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "username or email", request.getUsernameOrEmail()));
        if(!cus.isStatus()) throw new ApiException(HttpStatus.FORBIDDEN, "Customer is forbidden!");
        var jwtToken = jwtService.generateToken(cus, UserType.CUSTOMER.toString());
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(jwtToken);
        return authenticationResponse;
    }

    public AuthenticationResponse authenticateClinicAccount(AuthenticationRequest request) {
        ClinicAccount acc = determineClinicAcc(request.getUsernameOrEmail());
        customUserDetailsService.setUserType(acc.userType());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()));
        var jwtToken = jwtService.generateToken(acc.object(), acc.userType().toString());

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(jwtToken);
        return authenticationResponse;
    }

    @Override
    public String registerCustomerAccount(CustomerRegisterRequest request) {
        // add check if username already exists
        if (customerRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username or email is already exist!");
        }
        if(customerRepository.existsByPhone(request.getPhone()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Phone number is already used");

        Customer user = new Customer();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        user.setDob(request.getDob());
        user.setAddress(request.getAddress());
        customerRepository.save(user);
        return "Account created successfully";
    }

    private ClinicAccount determineClinicAcc(String request) {
        if (dentistRepository.existsByEmailOrUsername(request, request)) {
            var dentist = dentistRepository.findByUsernameOrEmail(request, request)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dentist cannot found!"));
            if(!dentist.getStatus().equals(Status.INACTIVE)) throw new ApiException(HttpStatus.FORBIDDEN, "Dentist is forbidden!");
            return new ClinicAccount(dentist, UserType.DENTIST);
        }else if (staffRepository.existsByEmailOrUsername(request, request)) {
            var staff = staffRepository.findByUsernameOrEmail(request, request)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Staff cannot found!"));
            if(!staff.getStatus().equals(Status.INACTIVE)) throw new ApiException(HttpStatus.FORBIDDEN, "Staff is forbidden!");
            return new ClinicAccount(staff, UserType.STAFF);
        }else if (ownerRepository.existsByEmailOrUsername(request, request)) {
            var owner = ownerRepository.findByUsernameOrEmail(request, request)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner cannot found!"));
            if(!owner.getStatus().equals(Status.INACTIVE)) throw new ApiException(HttpStatus.FORBIDDEN, "Owner is forbidden!");
            return new ClinicAccount(owner, UserType.OWNER);
        }
        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic account not found with username/email: " + request);
    }


}
