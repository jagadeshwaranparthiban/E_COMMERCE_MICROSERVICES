package com.jagadesh.authservice.dto;

public record PasswordChangeRequestDto(String token, String newPassword) {
}
