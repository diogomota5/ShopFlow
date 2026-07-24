package com.shopflow.backend.dto.request;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record AssessmentRequest(
        @NotNull(message = "O ID do produto é obrigatório")
        UUID productId,

        @NotNull(message = "A nota é obrigatória")
        @Min(value = 1, message = "A nota mínima é 1")
        @Max(value = 5, message = "A nota máxima é 5")
        Integer rating,

        @NotBlank(message = "O título da avaliação é obrigatório")
        @Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
        String title,

        String comment) {}
