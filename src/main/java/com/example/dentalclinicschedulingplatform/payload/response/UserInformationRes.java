package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String dob;
    private String address;
}
