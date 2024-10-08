package com.example.dentalclinicschedulingplatform.entity;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
public enum UserType {
    ADMIN("ADMIN"),
    CUSTOMER("CUSTOMER"),
    OWNER("OWNER"),
    STAFF("STAFF"),
    DENTIST("DENTIST");

    private final String type;

    @Override
    public String toString() {
        return type;
    }
}
