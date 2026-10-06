package com.shopflow.backend.dto.response;

import com.shopflow.backend.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    OrderStatus status,
    BigDecimal subtotal,
    BigDecimal discount,
    BigDecimal total,
    String shippingZipCode,
    String shippingStreet,
    String shippingNumber,
    String shippingComplement,
    String shippingNeighborhood,
    String shippingCity,
    String shippingState,
    String trackingCode,
    String notes,
    List<OrderItemResponse> items,
    List<OrderStatusHistoryResponse> statusHistory,
    LocalDateTime createdAt) {}
