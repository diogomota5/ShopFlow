package com.shopflow.backend.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderRequest(
    @NotNull(message = "o endereço de entrega é obrigatório")
    UUID addressId,
    String notes,
    String couponCode
) {}
