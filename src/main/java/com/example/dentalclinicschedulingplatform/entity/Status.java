package com.example.dentalclinicschedulingplatform.entity;

import com.example.dentalclinicschedulingplatform.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@ToString
public enum Status {
    PENDING("PENDING"),
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String status;

    public static void isValid(String status) {
        if (status == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Status is not valid");
        }
        for (Status s : Status.values()) {
            if (!s.status.equals(status)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Status is not valid");
            }
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "Status is not valid");
    }
}
