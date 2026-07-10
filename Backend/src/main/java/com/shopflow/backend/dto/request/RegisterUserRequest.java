package com.shopflow.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(@NotBlank(message = "Nome é obrigatório")
                                  @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
                                  String name,

                                  @NotBlank(message = "E-mail é obrigatório")
                                  @Email(message = "E-mail inválido")
                                  String email,

                                  @NotBlank(message = "Senha é obrigatória")
                                  @Size(min = 6, max = 100, message = "Senha deve ter no mínimo 6 caracteres")
                                  String password,

                                  @Size(max = 20, message = "Telefone inválido")
                                  String telephone)
{
}
