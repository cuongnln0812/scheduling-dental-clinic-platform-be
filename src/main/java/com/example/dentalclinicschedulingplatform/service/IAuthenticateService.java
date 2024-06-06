package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.AuthenticationRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CustomerRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.AuthenticationResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CustomerRegisterResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;

public interface IAuthenticateService {
    AuthenticationResponse authenticateCustomerAccount(AuthenticationRequest request);
    AuthenticationResponse authenticateClinicAccount(AuthenticationRequest request);
    String registerCustomerAccount(CustomerRegisterRequest request);
    UserInformationRes getUserInfo();
}
