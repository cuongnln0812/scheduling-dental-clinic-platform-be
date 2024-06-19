package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.example.dentalclinicschedulingplatform.utils.AppConstants.EMAIL_REGEX;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerRegisterRequest {
    @NotBlank(message = "Owner name must not be blank")
    private String fullName;
    @NotBlank(message = "Owner email must not be blank")
    @Pattern(regexp = EMAIL_REGEX, message = "Owner email is not valid")
    private String email;
    @Size(min = 10, max = 12, message = "Owner phone number must be between 10 and 12 digits")
    @Pattern(regexp = "^\\d+$", message = "Owner phone number must be numeric")
    @NotBlank(message = "Owner phone must not be blank")
    private String phone;
}
