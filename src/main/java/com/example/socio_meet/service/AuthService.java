package com.example.socio_meet.service;

import com.example.socio_meet.config.JwtUtil;
import com.example.socio_meet.dto.AuthResponse;
import com.example.socio_meet.dto.LoginRequest;
import com.example.socio_meet.dto.RegisterRequest;
import com.example.socio_meet.model.RefreshToken;
import com.example.socio_meet.model.User;
import com.example.socio_meet.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public void signup(RegisterRequest request){
        if (userRepository.existsByEmail(request.email())){
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of("ROLE_USER"));
        user.setEnabled(true);
        user.setLocked(false);

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),
                request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtUtil.generateToken(user.getId(), user.getRoles());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(token, refreshToken.getToken(),
                "Bearer", jwtUtil.getExpirationDate(token).getTime(),
                user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
