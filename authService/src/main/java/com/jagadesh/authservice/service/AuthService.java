package com.jagadesh.authservice.service;

import com.jagadesh.authservice.dto.AuthRequestDto;
import com.jagadesh.authservice.dto.AuthResponseDto;
import com.jagadesh.authservice.dto.RefreshTokenRequestDto;
import com.jagadesh.authservice.dto.SignUpRequestDto;
import com.jagadesh.authservice.exception.InvalidTokenException;
import com.jagadesh.authservice.model.AuthAccount;
import com.jagadesh.authservice.model.PasswordResetToken;
import com.jagadesh.authservice.model.RefreshToken;
import com.jagadesh.authservice.repository.AuthAccountRepo;
import com.jagadesh.authservice.repository.PasswordResetTokenRepo;
import com.jagadesh.authservice.repository.RefreshTokenRepo;
import com.jagadesh.authservice.util.JwtUtil;
import com.jagadesh.event.user.UserCreatedEvent;
import com.jagadesh.event.user.UserPasswordChangedEvent;
import com.jagadesh.event.user.UserPasswordResetRequestedEvent;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final long refreshTokenExpirationTime = 7L;

    private AuthAccountRepo authAccountRepo;
    private RefreshTokenRepo refreshTokenRepo;
    private AuthEventPublisher authEventPublisher;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private AuthenticationManager authenticationManager;
    private RefreshTokenService refreshTokenService;
    private PasswordResetTokenRepo passwordResetTokenRepo;

    public AuthService(
            AuthAccountRepo authAccountRepo,
            RefreshTokenRepo refreshTokenRepo,
            AuthEventPublisher authEventPublisher,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            AuthenticationManager authenticationManager,
            RefreshTokenService refreshTokenService,
            PasswordResetTokenRepo passwordResetTokenRepo
    ) {
        this.authAccountRepo = authAccountRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.authEventPublisher = authEventPublisher;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetTokenRepo = passwordResetTokenRepo;
    }

    private static final SecureRandom secureRandom = new SecureRandom();

    public String generatePasswordResetToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    //sign in
    public AuthResponseDto signIn(AuthRequestDto authRequestDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequestDto.username(),
                    authRequestDto.password()
            ));

            Optional<AuthAccount> authAccount = authAccountRepo.findByUsername(authRequestDto.username());
            String accessToken = jwtUtil.generateToken(authRequestDto.username());
            String refreshToken = refreshTokenService.generateRefreshToken(authAccount.get().getUserId());

            return new AuthResponseDto(accessToken, refreshToken);
        }catch (BadCredentialsException ex){
            throw new BadCredentialsException("Invalid credentials.");
        }
    }

    //sign up
    public String signUp(SignUpRequestDto signUpRequestDto) {
        if (authAccountRepo.findByUsername(signUpRequestDto.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }
        String userId = "USR".concat(UUID.randomUUID().toString().replace("-", "").substring(9));
        String encodedPassword = passwordEncoder.encode(signUpRequestDto.password());
        AuthAccount authAccount = authAccountRepo.save(
                new AuthAccount(
                        userId,
                        signUpRequestDto.username(),
                        signUpRequestDto.email(),
                        encodedPassword
                )
        );
        authEventPublisher.publishUserCreatedEvent( new UserCreatedEvent(
                UUID.randomUUID().toString(),
                authAccount.getUserId(),
                authAccount.getUsername(),
                signUpRequestDto.email(),
                Instant.now()
        ));
        return "User registered successfully.";
    }

    //refresh
    public AuthResponseDto getNewRefreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        RefreshToken storedToken = refreshTokenService.validateRefreshToken(refreshTokenRequestDto.refreshToken());
        String existingUsername = authAccountRepo.findUsernameByUserId(storedToken.getUserId()).getUsername();

        refreshTokenRepo.delete(storedToken);
        String newAccessToken = jwtUtil.generateToken(existingUsername);

        String newRefreshToken = refreshTokenService.generateRefreshToken(storedToken.getUserId());
        return new AuthResponseDto(newAccessToken, newRefreshToken);
    }

    //logOutUser
    public void logOutUser(RefreshTokenRequestDto refreshTokenRequestDto) {
        refreshTokenService.deleteToken(refreshTokenRequestDto.refreshToken());
    }

    public void sendPasswordResetToken(String email) {
        AuthAccount authAccount = authAccountRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No account associated with the provided email."));

        String token = generatePasswordResetToken();
        Instant createdAt = Instant.now();
        Instant expiresAt = createdAt.plus(10, ChronoUnit.MINUTES);

        PasswordResetToken passwordResetToken = new PasswordResetToken(
                authAccount.getUserId(),
                email,
                token,
                createdAt,
                expiresAt
        );
        passwordResetTokenRepo.save(passwordResetToken);
        authEventPublisher.publishUserPasswordResetRequestedEvent(new UserPasswordResetRequestedEvent(
                UUID.randomUUID().toString(),
                authAccount.getUserId(),
                token,
                email,
                createdAt,
                expiresAt
        ));
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepo.findByTokenHash(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token."));

        if (passwordResetToken.getExpiresAt().isBefore(Instant.now())) {
            passwordResetTokenRepo.delete(passwordResetToken);
            throw new InvalidTokenException("Password reset token has expired.");
        }

        AuthAccount authAccount = authAccountRepo.findByUserId(passwordResetToken.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User account not found."));

        String encodedPassword = passwordEncoder.encode(newPassword);
        authAccount.updatePassword(encodedPassword);
        authAccountRepo.save(authAccount);
        passwordResetTokenRepo.delete(passwordResetToken);

        authEventPublisher.publishUserPasswordChangedEvent( new UserPasswordChangedEvent(
                UUID.randomUUID().toString(),
                authAccount.getUserId(),
                passwordResetToken.getEmailId(),
                Instant.now()
        ));
    }
}
