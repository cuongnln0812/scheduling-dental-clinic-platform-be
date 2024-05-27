package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.AuthenticationRequest;
import com.example.dentalclinicschedulingplatform.payload.response.AuthenticationResponse;
import org.springframework.stereotype.Service;

public interface IAuthenticateService {
    AuthenticationResponse authenticateCustomerAccount(AuthenticationRequest request);
    AuthenticationResponse authenticateClinicAccount(AuthenticationRequest request);
}
