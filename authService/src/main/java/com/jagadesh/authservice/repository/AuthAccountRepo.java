package com.jagadesh.authservice.repository;

import com.jagadesh.authservice.model.AuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AuthAccountRepo extends JpaRepository<AuthAccount, String> {
    Optional<AuthAccount> findByUsername(String username);

    AuthAccount findUsernameByUserId(String userId);

    Optional<AuthAccount> findByUserId(String userId);

    Optional<AuthAccount> findByEmail(String email);
}
