package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class StaffUpdateRequest {
    @NotNull(message = "Staff Id can not be null")
    private Long id;

    private String fullName;

//    private String username;

    @Pattern(regexp="^(?=.*[A-Z])(?=.*\\d).{6,}$",
            message="Password must be at least 6 characters long, containing at least one uppercase letter and one digit")
    private String password;

    @Pattern(regexp="^[0-9]{10,12}$", message="Invalid phone number")
    private String phone;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String address;

    private String gender;

    private String avatar;

    private Long clinicBranchId;
}
