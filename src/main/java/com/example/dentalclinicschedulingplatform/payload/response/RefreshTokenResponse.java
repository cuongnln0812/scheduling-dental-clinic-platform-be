package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenResponse {
    private String token;
    private String refreshToken;
}
