package com.example.dentalclinicschedulingplatform.payload.request;

import com.example.dentalclinicschedulingplatform.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DentistDetailRequest {
    @NotNull(message = "Id must not be null")
    private Long id;
    @NotBlank(message = "Name must not be blank")
    private String fullName;
    @NotBlank(message = "Username must not be blank")
    private String username;
    @NotBlank(message = "Email must not be blank")
    private String email;
    @NotBlank(message = "Address must not be blank")
    private String address;
    @NotBlank(message = "Phone must not be blank")
    private String phone;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Date of birth must not be null")
    private LocalDate dob;
    @NotBlank(message = "Gender must not be blank")
    private String gender;
    @NotBlank(message = "Description must not be blank")
    private String description;
    @NotBlank(message = "Specialty must not be blank")
    private String specialty;
    @NotBlank(message = "Experience must not be blank")
    private String experience;
    @NotBlank(message = "Description must not be blank")
    private String avatar;
    @NotBlank(message = "Status must not be blank")
    private Status status;
}
