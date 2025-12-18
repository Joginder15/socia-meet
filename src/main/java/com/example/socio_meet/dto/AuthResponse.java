package com.example.socio_meet.dto;

public record AuthResponse(String accessToken, String refreshToken, long expiresIn) {}
