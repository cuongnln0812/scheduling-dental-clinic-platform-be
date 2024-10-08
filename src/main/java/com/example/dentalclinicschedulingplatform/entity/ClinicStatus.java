package com.example.dentalclinicschedulingplatform.entity;

import com.example.dentalclinicschedulingplatform.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ClinicStatus {
    PENDING("PENDING"),
    DENIED("DENIED"),
    APPROVED("APPROVED"),
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String status;

    @Override
    public String toString() {
        return status;
    }

    public static void isValid(ClinicStatus status) {
        if (status == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "ClinicStatus is not valid");
        }
        for (ClinicStatus s : ClinicStatus.values()) {
            if (s.status.equals(status.name())) {
                return;
            }
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "ClinicStatus is not valid");
    }
}
