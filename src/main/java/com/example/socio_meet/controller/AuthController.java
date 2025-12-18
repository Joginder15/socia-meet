package com.example.socio_meet.controller;

import com.example.socio_meet.dto.LoginRequest;
import com.example.socio_meet.dto.RegisterRequest;
import com.example.socio_meet.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;


    public AuthController(AuthService service) {
        this.service = service;
    }

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
        } catch (BadCredentialsException e){
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
