package com.example.dentalclinicschedulingplatform.payload.response;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerViewResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String address;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String avatar;
    private boolean status;
}
