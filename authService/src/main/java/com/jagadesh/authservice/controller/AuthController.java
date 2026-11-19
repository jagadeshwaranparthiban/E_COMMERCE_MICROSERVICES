package com.jagadesh.authservice.controller;

import com.jagadesh.authservice.dto.*;
import com.jagadesh.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //sign in
    @PostMapping("/signin")
    public ResponseEntity<AuthResponseDto> signIn(@RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(authService.signIn(authRequestDto));
    }

    //sign up
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<SuccessResponse> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        return ResponseEntity.ok(authService.signUp(signUpRequestDto));
    }

    //refresh
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@RequestBody RefreshTokenRequestDto refreshToken) {
        return ResponseEntity.ok(authService.getNewRefreshToken(refreshToken));
    }

    //logOutUser
    @PostMapping("/signout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        authService.logOutUser(refreshTokenRequestDto);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PutMapping("/forgot-password/{email}")
    public ResponseEntity<SuccessResponse> forgotPassword(@PathVariable(name = "email") String email) {
        authService.sendPasswordResetToken(email);
        return ResponseEntity.ok(new SuccessResponse("Password reset link has been sent to the registered mail."));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<SuccessResponse> resetPassword(@RequestBody PasswordChangeRequestDto passwordChangeRequestDto) {
        authService.resetPassword(
                passwordChangeRequestDto.token(),
                passwordChangeRequestDto.newPassword()
        );
        return ResponseEntity.ok(new SuccessResponse("Password has been reset successfully."));
    }

    @GetMapping("/health")
    public ResponseEntity<String> checkHealth() {
        return ResponseEntity.ok("Auth Service is up and running");
    }
}
