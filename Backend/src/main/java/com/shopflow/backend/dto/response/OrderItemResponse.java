package com.shopflow.backend.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID productId,
        String productName,
        BigDecimal unitPrice,
        String imageUrl,
        Integer quantity,
        BigDecimal subtotal
) {}
