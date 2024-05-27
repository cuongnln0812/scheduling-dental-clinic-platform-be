package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.AuthenticationRequest;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @PostMapping("/authenticate/customer")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticateCustomerAccount(request));
    }

//    @PostMapping("/change-password")
//    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeReq request) {
//        authenticationService.changePassword(request.getOldPassword(), request.getNewPassword());
//        return ResponseUtils.ok("Password changed successfully", HttpStatus.OK);
//    }

//    @GetMapping("/user-information")
//    public ResponseEntity<?> getUserInfo() {
//        UserAuthRes user = authenticationService.getUserInfo();
//        return ResponseUtils.response(user, "User information retrieved successfully!", HttpStatus.OK);
//    }

}
