package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClinicRegisterRequest {
    @NotBlank(message = "Clinic name must not be blank")
    private String clinicName;
    @NotBlank(message = "Address must not be blank")
    private String address;
    @NotBlank(message = "City must not be blank")
    private String city;
    private String clinicPhone;
    @NotBlank(message = "Registration must not be blank")
    private String clinicRegistration;
    private String websiteUrl;
    private String clinicImage;
    private OwnerRegisterRequest ownerRegisterRequest;
}
