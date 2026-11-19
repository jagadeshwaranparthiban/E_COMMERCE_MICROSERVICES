package com.jagadesh.userservice.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "users")
public class User {
    @Id
    private String userId;

    private String userName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "is_verified")
    private Boolean verified;

    @Enumerated(EnumType.STRING)
    private UserAccountStatus status;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Instant createdAt;
    private Instant updatedAt;

    protected User() {}

    public User(String userId, String email, String userName) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.verified = false;
        this.status = UserAccountStatus.ACTIVE;
        this.role = UserRole.USER;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void verified() {
        this.verified = true;
    }

    public void activate() {
        this.status = UserAccountStatus.ACTIVE;
    }

    public void suspend() {
        this.status = UserAccountStatus.SUSPENDED;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Boolean isVerified() {
        return verified;
    }

    public UserAccountStatus getStatus() {
        return status;
    }

    public String getUserName() {
        return userName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public UserRole getRole() {
        return role;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
