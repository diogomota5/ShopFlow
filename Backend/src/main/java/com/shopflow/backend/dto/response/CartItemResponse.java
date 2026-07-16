package com.shopflow.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CartItemResponse(UUID id,
                               UUID productId,
                               String productName,
                               String productImageUrl,
                               BigDecimal price,
                               BigDecimal originalPrice,
                               Integer quantity,
                               BigDecimal subtotal,
                               LocalDateTime addedAt) {
}
