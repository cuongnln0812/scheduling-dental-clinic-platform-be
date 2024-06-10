package com.example.dentalclinicschedulingplatform.payload.request;

import com.example.dentalclinicschedulingplatform.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static com.example.dentalclinicschedulingplatform.utils.AppConstants.EMAIL_REGEX;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DentistUpdateRequest {
    @NotNull(message = "Id must not be null")
    private Long id;
    @NotBlank(message = "Name must not be blank")
    private String fullName;
    @NotBlank(message = "Username must not be blank")
    private String username;
    @NotBlank(message = "Email must not be blank")
    @Pattern(regexp = EMAIL_REGEX, message = "Email is not valid")
    private String email;
    @NotBlank(message = "Gender must not be blank")
    private String gender;
    @Size(min = 10, max = 12, message = "Phone number must be between 10 and 12 digits")
    @Pattern(regexp = "^\\d+$", message = "Phone number must be numeric")
    @NotBlank(message = "Phone must not be blank")
    private String phone;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Date of birth must not be null")
    private LocalDate dob;
    @NotBlank(message = "Address must not be blank")
    private String address;
    @NotBlank(message = "Description must not be blank")
    private String description;
    @NotBlank(message = "Specialty must not be blank")
    private String specialty;
    @NotBlank(message = "Experience must not be blank")
    private String experience;
    @NotBlank(message = "Description must not be blank")
    private String avatar;
    @NotNull(message = "Status must not be null")
    private Status status;
    @NotNull(message = "Clinic branch must not be blank")
    private Long branchId;
}
