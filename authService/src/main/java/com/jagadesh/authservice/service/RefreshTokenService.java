package com.jagadesh.authservice.service;

import com.jagadesh.authservice.exception.InvalidTokenException;
import com.jagadesh.authservice.model.RefreshToken;
import com.jagadesh.authservice.repository.AuthAccountRepo;
import com.jagadesh.authservice.repository.RefreshTokenRepo;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final long refreshTokenExpirationTime = 7L;
    private RefreshTokenRepo refreshTokenRepo;

    public RefreshTokenService(RefreshTokenRepo refreshTokenRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
    }

    public String generateRefreshToken(String userId) {
        RefreshToken existingToken = refreshTokenRepo.findByUserId(userId).orElse(null);
        if(existingToken != null){
            refreshTokenRepo.delete(existingToken);
        }
        String refreshToken = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        RefreshToken newRefreshToken = new RefreshToken(
                userId,
                refreshToken,
                Instant.now().plus(refreshTokenExpirationTime, ChronoUnit.DAYS) // 7 days
        );
        refreshTokenRepo.save(newRefreshToken);
        return refreshToken;
    }

    public RefreshToken validateRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepo.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token."));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepo.delete(token);
            throw new IllegalArgumentException("Refresh token has expired.");
        }

        return token;
    }

    public void deleteToken(String refreshToken) {
        Optional<RefreshToken> token = refreshTokenRepo.findByToken(refreshToken);
        if(token.isPresent()) {
            refreshTokenRepo.delete(token.get());
        }else {
            throw new InvalidTokenException("Token not found");
        }
    }
}
