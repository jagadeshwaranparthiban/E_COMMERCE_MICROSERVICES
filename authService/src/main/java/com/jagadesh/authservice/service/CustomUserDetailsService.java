package com.jagadesh.authservice.service;

import com.jagadesh.authservice.model.AuthAccount;
import com.jagadesh.authservice.repository.AuthAccountRepo;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private AuthAccountRepo authAccountRepo;

    public CustomUserDetailsService(AuthAccountRepo authAccountRepo) {
        this.authAccountRepo = authAccountRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthAccount authAccount = authAccountRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new User(authAccount.getUsername(), authAccount.getPasswordHash(), new ArrayList<>());
    }
}
