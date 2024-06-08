package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.Length;

import java.time.LocalDate;

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
    private String branchName;
    private Status status;
}
