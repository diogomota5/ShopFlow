package com.shopflow.backend.dto.request;

import org.hibernate.validator.constraints.NotEmpty;

public record LoginRequest(@NotEmpty(message = "E-mail é obrigatório") String email,
                           @NotEmpty(message = "Senha é obrigatória") String password) {
}
