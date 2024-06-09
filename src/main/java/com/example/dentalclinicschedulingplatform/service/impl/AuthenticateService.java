package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.AuthenticationRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CustomerRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.AuthenticationResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CustomerRegisterResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;
import com.example.dentalclinicschedulingplatform.repository.CustomerRepository;
import com.example.dentalclinicschedulingplatform.repository.DentistRepository;
import com.example.dentalclinicschedulingplatform.repository.OwnerRepository;
import com.example.dentalclinicschedulingplatform.repository.StaffRepository;
import com.example.dentalclinicschedulingplatform.security.JwtService;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import com.example.dentalclinicschedulingplatform.utils.SecurityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticateService implements IAuthenticateService {
    private final JwtService jwtService;
    private final PasswordEncoder  passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final DentistRepository dentistRepository;
    private final StaffRepository staffRepository;
    private final OwnerRepository ownerRepository;

    public AuthenticateService(JwtService jwtService, PasswordEncoder passwordEncoder, ModelMapper modelMapper, AuthenticationManager authenticationManager, CustomerRepository customerRepository, DentistRepository dentistRepository, StaffRepository staffRepository, OwnerRepository ownerRepository) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.customerRepository = customerRepository;
        this.dentistRepository = dentistRepository;
        this.staffRepository = staffRepository;
        this.ownerRepository = ownerRepository;
    }

    public AuthenticationResponse authenticateAccount(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()));

        var jwtToken = jwtService.generateToken(authentication);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(jwtToken);
        return authenticationResponse;
    }

    @Override
    @Transactional
    public CustomerRegisterResponse registerCustomerAccount(CustomerRegisterRequest request) {
        // add check if username already exists
        if(isUsernameOrEmailExisted(request.getUsername(), request.getEmail()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username/Email is already used");
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
        user.setStatus(true);
        user = customerRepository.save(user);
        return modelMapper.map(user, CustomerRegisterResponse.class);
    }

    @Override
    public UserInformationRes getUserInfo(){
        String role = SecurityUtils.getRoleName();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInformationRes res = new UserInformationRes();
        if(role.equals("ROLE_" + UserType.CUSTOMER)){
            Customer user = customerRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            res = modelMapper.map(user, UserInformationRes.class);
            res.setRole(UserType.CUSTOMER.toString());
        }else if(role.equals("ROLE_" + UserType.DENTIST)){
            Dentist user = dentistRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            res = modelMapper.map(user, UserInformationRes.class);
            res.setRole(UserType.DENTIST.toString());
        }else if(role.equals("ROLE_" + UserType.STAFF)){
            ClinicStaff user = staffRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            res = modelMapper.map(user, UserInformationRes.class);
            res.setRole(UserType.STAFF.toString());
        }else if(role.equals("ROLE_" + UserType.OWNER)){
            ClinicOwner user = ownerRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            res = modelMapper.map(user, UserInformationRes.class);
            res.setRole(UserType.OWNER.toString());
        }
        return res;
    }

    boolean isUsernameOrEmailExisted(String username, String email) {
        return customerRepository.existsByUsernameOrEmail(username, email) ||
                dentistRepository.existsByEmailOrUsername(username, email) ||
                staffRepository.existsByEmailOrUsername(username, email) ||
                ownerRepository.existsByEmailOrUsername(username, email);
    }

}
