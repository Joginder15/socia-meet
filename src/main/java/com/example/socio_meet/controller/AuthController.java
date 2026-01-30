package com.example.socio_meet.controller;

import com.example.socio_meet.dto.LoginRequest;
import com.example.socio_meet.dto.RegisterRequest;
import com.example.socio_meet.service.AuthService;
import com.example.socio_meet.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody RegisterRequest request){
        try {
            service.signup(request);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        try {
            return ResponseEntity.ok(service.login(request));
        } catch (BadCredentialsException exception){
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("refresh-token") String refreshToken){
        refreshTokenService.deleteByToken(refreshToken);
        return ResponseEntity.ok("Logged out successfully");
    }
}
