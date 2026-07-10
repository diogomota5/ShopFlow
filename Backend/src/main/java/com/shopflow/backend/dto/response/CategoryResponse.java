package com.shopflow.backend.dto.response;

import com.shopflow.backend.entity.Category;

import java.util.UUID;

public record CategoryResponse(UUID id,
                               String name,
                               String description,
                               String slug,
                               String imageUrl,
                               boolean active) {

    public static CategoryResponse fromEntity(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getSlug(),
                category.getImageUrl(),
                category.isActive()
        );
    }
}