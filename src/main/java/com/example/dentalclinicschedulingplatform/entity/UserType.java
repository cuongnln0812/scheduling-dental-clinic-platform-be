package com.example.dentalclinicschedulingplatform.entity;

public enum UserType {
    ADMIN("ADMIN"),
    CUSTOMER("CUSTOMER"),
    OWNER("OWNER"),
    STAFF("STAFF"),
    DENTIST("DENTIST");

    private final String type;

    UserType(String string) {
        type = string;
    }

    @Override
    public String toString() {
        return type;
    }
}
