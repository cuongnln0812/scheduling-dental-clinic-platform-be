package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.AuthenticationRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CustomerRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.request.RefreshTokenRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.payload.request.PasswordChangeRequest;
import com.example.dentalclinicschedulingplatform.payload.request.UserInfoUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.AuthenticationResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CustomerRegisterResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
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

}
