package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClinicListResponse {
    private Long clinicId;
    private String clinicName;
    private String address;
    private String city;
    private String phone;
    private ClinicStatus status;
}
