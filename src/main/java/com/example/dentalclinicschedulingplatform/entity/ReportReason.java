package com.example.dentalclinicschedulingplatform.entity;

import com.example.dentalclinicschedulingplatform.exception.ApiException;
import org.springframework.http.HttpStatus;

public enum ReportReason {
    SPAM("Spam"),
    INAPPROPRIATE_CONTENT("Inappropriate content"),
    ABUSIVE_BEHAVIOR ("Abusive behavior"),
    FALSE_INFORMATION ("False information"),
    HARASSMENT("Harrassment"),
    DISCRIMINATION("Discrimination"),
    VIOLATION_OF_TERMS("Violation of term");
    private final String reason;

    ReportReason(String string) {
        reason = string;
    }

    @Override
    public String toString() {
        return reason;
    }

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
