package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountListResponse {
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private LocalDate dob;
    private String roleName;
    private String status;
}
