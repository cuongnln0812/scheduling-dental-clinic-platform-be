package com.example.dentalclinicschedulingplatform.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private HttpStatus status;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private boolean success = true;
    private String message;
    private T data;

    // General constructor for all response types
    public ApiResponse(HttpStatus status, boolean success, String message, T data) {
        this.status = status;
        this.message = message;
        this.success = success;
        this.data = data;
    }

    // Overloaded constructor for no data
    public ApiResponse(HttpStatus status, boolean success, String message) {
        this(status, success, message, null);
    }

    // Overloaded constructor for default success
    public ApiResponse(HttpStatus status, String message, T data) {
        this(status, true, message,  data);
    }

    // Overloaded constructor for default success and no data
    public ApiResponse(HttpStatus status, String message) {
        this(status, true, message, null);
    }
}
