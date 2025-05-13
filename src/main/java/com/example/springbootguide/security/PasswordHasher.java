package com.example.springbootguide.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);
        String rawPassword = "admin_JTFTghfw874";
        String hashedPassword = passwordEncoder.encode(rawPassword);
        System.out.println(hashedPassword);
        rawPassword = "user_Ouhrtgeo85";
        hashedPassword = passwordEncoder.encode(rawPassword);
        System.out.println(hashedPassword);
    }
}
