package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerRegisterRequest {
    @NotBlank(message = "Name must not be blank")
    private String fullName;
    @NotBlank(message = "Email must not be blank")
    private String email;
    @NotBlank(message = "Phone must not be blank")
    private String phone;
}
