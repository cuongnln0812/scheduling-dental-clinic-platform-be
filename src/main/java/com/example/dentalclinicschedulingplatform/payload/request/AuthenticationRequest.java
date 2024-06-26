package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotBlank(message = "Username must not be blank")
    private String username;
    @NotBlank(message = "Password must not blank")
    private String password;
}
