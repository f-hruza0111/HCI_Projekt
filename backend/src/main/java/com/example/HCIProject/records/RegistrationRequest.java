package com.example.HCIProject.records;

public record RegistrationRequest(
        String email,
        String username,
        String password
) {
}
