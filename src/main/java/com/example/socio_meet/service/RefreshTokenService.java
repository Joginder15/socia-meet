package com.example.socio_meet.service;

import com.example.socio_meet.model.RefreshToken;
import com.example.socio_meet.model.User;
import com.example.socio_meet.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60));
        refreshToken.setRevoked(false);

        User user = new User();
        user.setId(userId);
        refreshToken.setUser(user);

        return repository.save(refreshToken);
    }

    public void revokeToken(String token) {
        repository.findByToken(token).ifPresent(revToken ->{
            revToken.setRevoked(true);
            repository.save(revToken);
        });
    }

    @Transactional
    public void deleteByToken(String token){
        repository.deleteByToken(token);
    }




}
