package com.example.dentalclinicschedulingplatform.payload.request;

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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoUpdateRequest {
    @NotBlank(message = "Username must not be blank")
    private String username;
    @NotBlank(message = "Full name must not be blank")
    private String fullName;
    @NotBlank(message = "Email must not be blank")
    private String email;
    @NotBlank(message = "Gender must not be blank")
    private String gender;
    @Size(min = 10, max = 12, message = "Phone number must be between 10 and 12 digits")
    @Pattern(regexp = "^\\d+$", message = "Phone number must be numeric")
    @NotBlank(message = "Phone must not be blank")
    private String phone;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Dob must not be null")
    private LocalDate dob;
    @NotBlank(message = "Address must not be blank")
    private String address;
    private String avatar;
}
