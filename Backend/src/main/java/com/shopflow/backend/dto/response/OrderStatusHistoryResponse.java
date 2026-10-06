package com.shopflow.backend.dto.response;

import com.shopflow.backend.entity.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderStatusHistoryResponse(UUID id, OrderStatus status, String note, LocalDateTime createdAt) {
}
