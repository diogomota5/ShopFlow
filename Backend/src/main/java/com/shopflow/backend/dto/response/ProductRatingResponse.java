package com.shopflow.backend.dto.response;

public record ProductRatingResponse(
        Double averageRating,
        Long totalAssessments
) {}
