package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.AuthenticationRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CustomerRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.AuthenticationResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CustomerRegisterResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;

public interface IAuthenticateService {
    AuthenticationResponse authenticateAccount(AuthenticationRequest request);
    CustomerRegisterResponse registerCustomerAccount(CustomerRegisterRequest request);
    UserInformationRes getUserInfo();
    boolean isUsernameOrEmailExisted(String username, String email);
}
