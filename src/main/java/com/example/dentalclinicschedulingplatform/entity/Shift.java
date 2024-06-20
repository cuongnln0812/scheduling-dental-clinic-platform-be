package com.example.dentalclinicschedulingplatform.entity;

import com.example.dentalclinicschedulingplatform.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@ToString
public enum Shift {
    MORNING("MORNING"),
    AFTERNOON("AFTERNOON"),
    ALLDAY("ALLDAY")
    ;

    private final String shift;

    public static void isValid(Shift shift) {
        if (shift == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Status is not valid");
        }
        for (Shift s : Shift.values()) {
            if (s.shift.equals(shift.name())) {
                return;
            }
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "Status is not valid");
    }

}
