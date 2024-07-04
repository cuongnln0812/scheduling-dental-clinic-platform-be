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
public class BranchUpdateRequest {
    private Long id;
    private String branchName;
    private String address;
    private String city;
    @Pattern(regexp = "^$|^[0-9]{10,12}$", message = "Invalid phone number")
    private String phone;
}
