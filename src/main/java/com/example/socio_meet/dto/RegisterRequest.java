package com.example.socio_meet.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank String firstName, String lastName, @Email String email, @NotBlank @Size(min = 6) String password) {
}
