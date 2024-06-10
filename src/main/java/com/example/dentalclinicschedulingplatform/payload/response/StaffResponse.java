package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.Blog;
import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;
import com.example.dentalclinicschedulingplatform.entity.ClinicStaff;

import com.example.dentalclinicschedulingplatform.entity.Status;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponse {

    private Long id;
    private String fullName;
    private String email;
    private String username;
    //private String password;
    private String phone;
    private String address;
    private LocalDate dob;
    private String gender;
    private String avatar;
    private Status status;
    private String clinicBranchName;


    public StaffResponse(ClinicStaff clinicStaff){
        this.id = clinicStaff.getId();
        this.fullName = clinicStaff.getFullName();
        this.email = clinicStaff.getEmail();
        this.username = clinicStaff.getUsername();
        //this.password = clinicStaff.getPassword();
        this.phone = clinicStaff.getPhone();
        this.address = clinicStaff.getAddress();
        this.dob = clinicStaff.getDob();
        this.gender = clinicStaff.getGender();
        this.avatar = clinicStaff.getAvatar();
        this.status = clinicStaff.getStatus();
        this.clinicBranchName = clinicStaff.getClinicBranch().getBranchName();
    }
}
