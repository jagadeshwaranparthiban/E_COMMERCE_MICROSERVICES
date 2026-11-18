package com.jagadesh.authservice.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table( name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String tokenId;
    private String userId;
    private String emailId;
    private String tokenHash;
    private Boolean used;
    private Instant createdAt;
    private Instant expiresAt;

    public PasswordResetToken() {}

    public PasswordResetToken(String userId, String emailId, String tokenHash, Instant createdAt, Instant expiresAt) {
        this.userId = userId;
        this.emailId = emailId;
        this.tokenHash = tokenHash;
        this.used = false;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getTokenId() {
        return tokenId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Boolean getUsed() {
        return used;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public String getEmailId() {
        return emailId;
    }
}
