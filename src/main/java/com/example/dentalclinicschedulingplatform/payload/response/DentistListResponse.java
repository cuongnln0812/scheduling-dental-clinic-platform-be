package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DentistListResponse {
    private Long dentistId;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String branchName;
    private ClinicStatus status;
}
