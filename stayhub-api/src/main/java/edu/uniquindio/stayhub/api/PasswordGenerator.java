package edu.uniquindio.stayhub.api;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("Password for test@example.com: " + encoder.encode("test123"));
        System.out.println("Password for host@example.com: " + encoder.encode("host123"));
    }
}