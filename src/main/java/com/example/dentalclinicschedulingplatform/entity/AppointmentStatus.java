package com.example.dentalclinicschedulingplatform.entity;

import com.example.dentalclinicschedulingplatform.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@ToString
public enum AppointmentStatus {
    PENDING("PENDING"),
    DONE("DONE"),
    CANCELED("CANCELED");

    private final String status;

    public static void isValid(AppointmentStatus status) {
        if (status == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "ClinicStatus is not valid");
        }
        for (AppointmentStatus s : AppointmentStatus.values()) {
            if (s.status.equals(status.name())) {
                return;
            }
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "ClinicStatus is not valid");
    }
}
