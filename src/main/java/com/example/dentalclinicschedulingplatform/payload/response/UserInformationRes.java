package com.example.dentalclinicschedulingplatform.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInformationRes {
    private String username;
    private String fullName;
    private String email;
    private String gender;
    private String phone;
    private LocalDate dob;
    private String address;
    private String avatar;
    private String role;
}
