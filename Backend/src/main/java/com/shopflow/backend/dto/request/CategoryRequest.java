package com.shopflow.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(@NotBlank(message = "O nome é obrigatório")
                              @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
                              String name,

                              String description,

                              @Size(max = 500)
                              @Pattern(regexp = "^(http|https)://.*$", message = "URL da imagem inválida")
                              String imageUrl) {
}