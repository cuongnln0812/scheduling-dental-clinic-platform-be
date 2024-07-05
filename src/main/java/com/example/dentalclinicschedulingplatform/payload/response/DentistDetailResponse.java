package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DentistDetailResponse {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String address;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String description;
    private String specialty;
    private String experience;
    private String avatar;
    private Long branchId;
    private String branchName;
    private String city;
    private Long clinicId;
    private String clinicName;
    private ClinicStatus status;
}
