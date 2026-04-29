package com.shopflow.backend.dto.request;

import com.shopflow.backend.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

                                  @NotNull(message = "Informe se o perfil do usuário é User ou Admin")
                                  Role role,

                                  @Size(max = 20, message = "Telefone inválido")
                                  String telephone,

                                  @NotNull(message = "Informe se o usuário está ativo")
                                  Boolean active) {
}
