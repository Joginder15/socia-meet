package com.example.socio_meet.dto;

public record AuthResponse(String accessToken,
                           String refreshToken,
                           String tokenType,
                           long expiresIn,
                           String firstName,
                           String lastName,
                           String email) {}
