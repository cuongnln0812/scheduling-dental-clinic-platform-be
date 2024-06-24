package com.example.dentalclinicschedulingplatform.payload.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BranchCreateRequest {
    @NotBlank(message = "Clinic branch name must not be blank")
    private String branchName;
    @NotBlank(message = "Clinic branch address must not be blank")
    private String address;
    @NotBlank(message = "Clinic branch city must not be blank")
    private String city;
    @NotBlank(message="Phone number can not be blank")
    @Pattern(regexp="^[0-9]{10,12}$", message="Invalid phone number")
    private String phone;
}
