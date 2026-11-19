package com.jagadesh.authservice.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "auth_accounts")
public class AuthAccount {
    @Id
    private String userId;
    private String username;
    private String email;
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Enumerated(EnumType.STRING)
    private AuthUserRole role;

    private Instant createdAt;
    private Instant updatedAt;

    protected AuthAccount() {
    }

    public AuthAccount(String userId, String username, String email, String passwordHash, AuthUserRole role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = AccountStatus.ACTIVE;
        this.role = role;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void lockAccount() {
        this.status = AccountStatus.LOCKED;
    }

    public void unlockAccount() {
        this.status = AccountStatus.ACTIVE;
    }

    public void updatePasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.updatedAt = Instant.now();
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public AuthUserRole getRole() { return role; }

    public void updatePassword(String encodedPassword) {
        this.passwordHash = encodedPassword;
        this.updatedAt = Instant.now();
    }

    public String getEmail() {
        return email;
    }
}
