package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.AuthenticationRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CustomerRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.request.RefreshTokenRequest;
import com.example.dentalclinicschedulingplatform.payload.request.PasswordChangeRequest;
import com.example.dentalclinicschedulingplatform.payload.request.UserInfoUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.AuthenticationResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CustomerRegisterResponse;
import com.example.dentalclinicschedulingplatform.payload.response.RefreshTokenResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;

public interface IAuthenticateService {
    AuthenticationResponse authenticateAccount(AuthenticationRequest request);
    CustomerRegisterResponse registerCustomerAccount(CustomerRegisterRequest request);
    UserInformationRes getUserInfo();
    UserInformationRes updateUserInfo(UserInfoUpdateRequest request);
    String changePassword(PasswordChangeRequest request);
    boolean isUsernameOrEmailExisted(String username, String email);
    boolean checkPasswordChange(String oldPass, String newPass, String userPass);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    void logout(RefreshTokenRequest refreshToken);

}
