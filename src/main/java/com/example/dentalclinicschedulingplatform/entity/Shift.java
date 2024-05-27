package com.example.dentalclinicschedulingplatform.entity;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public enum Shift {
    MORNING("MORNING"),
    AFTERNOON("AFTERNOON"),
    ALLDAY("ALLDAY")
    ;

    private final String shift;

}
