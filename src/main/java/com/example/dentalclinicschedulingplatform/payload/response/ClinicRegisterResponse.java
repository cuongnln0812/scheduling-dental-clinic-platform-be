package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
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
    private String phone;
    private String clinicRegistration;
    private String websiteUrl;
    private String clinicImage;
    private ClinicStatus clinicStatus;
    private OwnerRegisterResponse ownerDetail;
}