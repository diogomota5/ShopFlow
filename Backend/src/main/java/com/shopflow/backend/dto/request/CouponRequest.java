package com.shopflow.backend.dto.request;

import com.shopflow.backend.entity.enums.DiscountType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponRequest(

    @NotBlank(message = "O código do cupom é obrigatório")
    @Size(max = 50, message = "O código deve ter no máximo 50 caracteres")
    String code,

    @NotNull(message = "O valor do desconto é obrigatório")
    @DecimalMin(value = "0.01", message = "O desconte deve ser maior que zero")
    BigDecimal discountValue,

    @NotNull(message = "O tipo do desconto é obrigatório")
    DiscountType discountType,

    BigDecimal minPurchaseValue,

    @NotNull(message = "A data de expiração é obrigatória")
    @Future(message = "A data de expiração deve ser no futuro")
    LocalDateTime expirationDate,

    Integer maxUses
) {}
