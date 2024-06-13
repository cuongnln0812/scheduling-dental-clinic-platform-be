package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.AuthenticationRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CustomerRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.request.RefreshTokenRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
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

//    @PostMapping("/change-password")
//    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeReq request) {
//        authenticationService.changePassword(request.getOldPassword(), request.getNewPassword());
//        return ResponseUtils.ok("Password changed successfully", HttpStatus.OK);
//    }

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

}
