package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.*;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.AuthenticationResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CustomerRegisterResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
public class AuthenticationController {

    private final IAuthenticateService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@Valid @RequestBody AuthenticationRequest request){
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Authenticate successfully!",
                authenticationService.authenticateAccount(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerRegisterResponse> > register(@Valid @RequestBody CustomerRegisterRequest request){
        ApiResponse<CustomerRegisterResponse> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Account registered successfully!",
                authenticationService.registerCustomerAccount(request));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/password-change")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK, authenticationService.changePassword(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/user-information")
    public ResponseEntity<ApiResponse<UserInformationRes>> updateUserInfo(@Valid @RequestBody UserInfoUpdateRequest request){
        ApiResponse<UserInformationRes> response = new ApiResponse<>(
                HttpStatus.OK,
                "Update information successfully!",
                authenticationService.updateUserInfo(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user-information")
    public ResponseEntity<ApiResponse<UserInformationRes>> getUserInfo() {
        ApiResponse<UserInformationRes> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get user information successfully",
                authenticationService.getUserInfo());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        ApiResponse<RefreshTokenResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Refresh token successfully!",
                authenticationService.refreshToken(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody RefreshTokenRequest request) {
        authenticationService.logout(request);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.OK,
                "Logged out successfully!",
                "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login-google")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> loginGoogle(@Valid @RequestBody LoginGoogleRequest request) {
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Google login successfully!",
                authenticationService.loginWithGoogle(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/recover-password")
    public ResponseEntity<ApiResponse<String>> recoverPassword(@Valid @RequestBody RecoverPasswordRequest request) {
        authenticationService.recoverPassword(request);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.OK,
                "Verification code sent to email successfully!",
                "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/verify-reset-password")
    public ResponseEntity<ApiResponse<String>> verifyResetPassword(@Valid @RequestBody VerifyResetPasswordRequest request) {
        authenticationService.verifyAndResetPassword(request);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.OK,
                "Password reset successfully!",
                "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
