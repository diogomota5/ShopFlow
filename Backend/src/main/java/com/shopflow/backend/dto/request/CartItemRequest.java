package com.shopflow.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CartItemRequest(

        @NotNull(message = "O produto é obrigatório")
        UUID productId,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
        Integer quantity) {
}
