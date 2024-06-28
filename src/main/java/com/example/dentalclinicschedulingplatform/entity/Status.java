package com.example.dentalclinicschedulingplatform.entity;

import com.example.dentalclinicschedulingplatform.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@ToString
public enum Status {
    PENDING("PENDING"),
    DENIED("DENIED"),
    APPROVED("APPROVED"),
    CANCELED("CANCELED"),
    DONE("DONE"),
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String status;

    public static void isValid(Status status) {
        if (status == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Status is not valid");
        }
        for (Status s : Status.values()) {
            if (s.status.equals(status.name())) {
                return;
            }
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "Status is not valid");
    }

    public static Status toggleStatus(Status userStatus) {
        if(userStatus.equals(Status.PENDING) || userStatus.equals(Status.DENIED))
            throw new ApiException(HttpStatus.CONFLICT, "Status cannot be changed");
        return userStatus.equals(Status.ACTIVE) ? Status.INACTIVE : Status.ACTIVE;
    }
}
