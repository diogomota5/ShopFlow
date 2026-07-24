package com.shopflow.backend.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssessmentResponse(
        UUID id,
        String userName,
        UUID productId,
        Integer rating,
        String title,
        String comment,
        Boolean verified,
        LocalDateTime createdAt) {
}
