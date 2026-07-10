package com.shopflow.backend.dto.response;

import com.shopflow.backend.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(UUID id,
                              String name,
                              String description,
                              BigDecimal price,
                              BigDecimal originalPrice,
                              Integer stock,
                              String sku,
                              String imageUrl,
                              Boolean active,
                              String categoryName,
                              UUID categoryId,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {

public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getOriginalPrice(),
                product.getStock(),
                product.getSku(),
                product.getImageUrl(),
                product.getActive(),
                product.getCategory().getName(),
                product.getCategory().getId(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}