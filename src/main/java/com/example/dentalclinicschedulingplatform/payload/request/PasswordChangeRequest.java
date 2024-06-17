package com.example.dentalclinicschedulingplatform.payload.request;

import com.example.dentalclinicschedulingplatform.utils.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {
    @NotBlank(message = "Old password must not be blank!")
    private String oldPassword;
    @NotBlank(message = "New password must not be blank!")
    @Pattern(regexp = AppConstants.PASSWORD_REGEX,
            message = "Password must be 8-12 characters with at least one uppercase letter, one number, and one special character (!@#$%^&*).")
    private String newPassword;
}
