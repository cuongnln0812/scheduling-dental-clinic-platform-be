package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApiResponse<T> {
    private HttpStatus status;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private T data;

    public ApiResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiResponse(HttpStatus status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
