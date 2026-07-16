package com.shopflow.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(UUID id,
                           List<CartItemResponse> items,
                           BigDecimal totalPrice,
                           Integer totalItems) {
}
