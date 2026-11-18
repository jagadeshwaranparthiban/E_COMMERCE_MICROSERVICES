package com.jagadesh.userservice.dto;

import java.time.Instant;

public record UserResponseDto(
        String userId,
        String userName,
        String email,
        String status,
        Boolean verified,
        Instant createdAt,
        Instant updatedAt
) { }
