package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.AuthenticationRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CustomerRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.AuthenticationResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@SecurityRequirement(name = "bearerAuth")
public class AuthenticationController {

    private final IAuthenticateService authenticationService;

    public AuthenticationController(IAuthenticateService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login/customer")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@Valid @RequestBody AuthenticationRequest request){
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Authenticate successfully!",
                authenticationService.authenticateCustomerAccount(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/login/clinic")
    public ResponseEntity<?> authenticateClinic(@Valid @RequestBody AuthenticationRequest request){
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Authenticate successfully!",
                authenticationService.authenticateClinicAccount(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String> > register(@Valid @RequestBody CustomerRegisterRequest request){
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.CREATED,
                authenticationService.registerCustomerAccount(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
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

}
