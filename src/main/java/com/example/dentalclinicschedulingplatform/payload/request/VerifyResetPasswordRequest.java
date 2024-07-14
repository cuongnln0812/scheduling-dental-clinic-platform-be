package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyResetPasswordRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String verificationCode;

    @NotBlank
    private String newPassword;
}
