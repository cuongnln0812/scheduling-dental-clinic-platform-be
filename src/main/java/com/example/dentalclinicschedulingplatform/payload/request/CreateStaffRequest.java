package com.example.dentalclinicschedulingplatform.payload.request;

import com.example.dentalclinicschedulingplatform.entity.Blog;
import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
public class CreateStaffRequest {

    @NotBlank(message="Name can not be blank")
    private String fullName;

    @NotBlank
    @Email(message="Invalid email address")
    private String email;

    @NotBlank(message = "User name can not be blank")
    private String username;

    @NotBlank(message="Password can not be blank")
    @Pattern(regexp="^(?=.*[A-Z])(?=.*\\d).{6,}$",
            message="Password must be at least 6 characters long, containing at least one uppercase letter and one digit")
    private String password;

    @NotBlank(message="Phone number can not be blank")
    @Pattern(regexp="^[0-9]{10,12}$", message="Invalid phone number")
    private String phone;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message="Day of birth can not be blank")
    private LocalDate dob;

    @NotBlank(message = "Address can not be blank")
    private String address;

    @NotBlank(message="Gender can not be blank")
    private String gender;

    private String avatar;

//    @NotBlank(message="Clinic branch can not be blank")
//    //@Size(min=1, message= "Clinic branch can not be blank")
//    private Long clinicBranchId;

    @NotNull(message = "Clinic branch ID cannot be null")
    private Long clinicBranchId;

}
