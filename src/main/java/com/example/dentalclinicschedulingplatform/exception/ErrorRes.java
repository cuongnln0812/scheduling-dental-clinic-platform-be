package com.example.dentalclinicschedulingplatform.exception;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrorRes {
    private LocalDateTime timestamp = LocalDateTime.now();
    private String status = "failed";
    private int httpStatus;
    private String message;
    private String details;
}
