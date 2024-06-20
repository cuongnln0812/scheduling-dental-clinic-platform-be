package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClinicRegisterResponse {
    private Long clinicId;
    private String clinicName;
    private String address;
    private String city;
    private String clinicPhone;
    private String clinicRegistration;
    private String websiteUrl;
    private String clinicImage;
    private Status clinicStatus;
    private String fullName;
    private String email;
    private String phone;
}