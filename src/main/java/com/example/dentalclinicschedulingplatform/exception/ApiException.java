package com.example.dentalclinicschedulingplatform.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ResponseStatus
public class ApiException extends RuntimeException{
    private HttpStatus status;
    private String message;

    public ApiException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}