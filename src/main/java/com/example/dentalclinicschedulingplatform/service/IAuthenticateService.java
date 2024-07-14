package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.*;
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
    boolean isUsernameExisted(String username);
    boolean checkPasswordChange(String oldPass, String newPass, String userPass);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    void logout(RefreshTokenRequest refreshToken);
    AuthenticationResponse loginWithGoogle(LoginGoogleRequest request);
    void recoverPassword(RecoverPasswordRequest request);
    void verifyAndResetPassword(VerifyResetPasswordRequest request);

}
