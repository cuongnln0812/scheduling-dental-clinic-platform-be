package com.example.dentalclinicschedulingplatform.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderGenerator {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String string = passwordEncoder.encode("Cuong123@");
        System.out.println(string);
    }
}