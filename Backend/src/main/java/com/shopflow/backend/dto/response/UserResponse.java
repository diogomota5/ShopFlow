package com.shopflow.backend.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String telephone,
        String role,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
