package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApiResponse<T> {
    private HttpStatus status;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private boolean success = true;
    private String message;
    private T data;

    //Only for exception response
    public ApiResponse(HttpStatus status, boolean success, String message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }
    //Only for exception response
    public ApiResponse(HttpStatus status, boolean success, String message, T data) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

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
