package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.Clinic;
import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerViewResponse {

    private Long ownerId;
    private String username;
    private String fullName;
    private String email;
    private String address;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String avatar;
    private ClinicStatus status;
    private Long clinicId;
}
