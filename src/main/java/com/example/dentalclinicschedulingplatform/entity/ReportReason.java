package com.example.dentalclinicschedulingplatform.entity;

import com.example.dentalclinicschedulingplatform.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@ToString
public enum ReportReason {
    SPAM("Spam"),
    INAPPROPRIATE_CONTENT("Inappropriate content"),
    ABUSIVE_BEHAVIOR ("Abusive behavior"),
    FALSE_INFORMATION ("False information"),
    HARASSMENT("Harassment"),
    DISCRIMINATION("Discrimination"),
    VIOLATION_OF_TERMS("Violation of term");
    private final String reason;

    public static void isValid(ReportReason reportReason) {
        if (reportReason == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Report reason is not valid");
        }
        for (ReportReason rr : ReportReason.values()) {
            if (rr.reason.equals(rr.name())) {
                return;
            }
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "Report reason is not valid");
    }
}
