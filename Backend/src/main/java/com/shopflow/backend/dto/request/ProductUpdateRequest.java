package com.shopflow.backend.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductUpdateRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String name,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "Preço deve ser maior que zero")
        BigDecimal price,

        @NotNull(message = "O preço original é obrigatório")
        @Positive(message = "Preço original deve ser maior que zero")
        BigDecimal originalPrice,

        @NotNull(message = "O estoque é obrigatório")
        @Min(value = 0, message = "Estoque não pode ser negativo")
        Integer stock,

        @NotBlank(message = "O SKU é obrigatório")
        @Size(max = 50, message = "O SKU deve conter no máximo 50 caracteres")
        String sku,

        @Size(max = 500)
        @Pattern(regexp = "^(http|https)://.*$", message = "URL da imagem inválida")
        String imageUrl,

        @NotNull(message = "A categoria é obrigatória")
        UUID categoryId) {
}
