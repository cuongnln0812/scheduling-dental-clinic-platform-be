package com.example.dentalclinicschedulingplatform.payload.request;

import com.example.dentalclinicschedulingplatform.entity.Blog;
import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
public class CreateStaffRequest {

    @NotBlank(message="Name cannot be blank")
    private String fullName;

    @NotBlank
    @Email(message="Invalid email address")
    private String email;

    @NotBlank(message="Password cannot be blank")
    @Pattern(regexp="^(?=.*[A-Z])(?=.*\\d).{6,}$",
            message="Password must be at least 6 characters long, containing at least one uppercase letter and one digit")
    private String password;

    @NotBlank(message="Phone number cannot be blank")
    @Pattern(regexp="^[0-9]{10,12}$", message="Invalid phone number")
    private String phone;

    @NotNull(message="Day of birth cannot be blank")
    private LocalDate dob;

    @NotBlank
    private String gender;

    @NotBlank
    private String avatar;

    @NotBlank
    private Long clinicBranchId;


}
