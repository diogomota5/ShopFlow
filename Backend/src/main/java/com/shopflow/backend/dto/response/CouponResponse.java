package com.shopflow.backend.dto.response;

import com.shopflow.backend.entity.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CouponResponse(
        UUID id,
        String code,
        BigDecimal discountValue,
        DiscountType discountType,
        BigDecimal minPurchaseValue,
        LocalDateTime expirationDate,
        Integer maxUses,
        Integer userCount,
        Boolean active) {}
