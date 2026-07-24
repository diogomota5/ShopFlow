package com.shopflow.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressesRequest(
        @Size(max = 50, message = "O apelido do endereço deve ter no máximo 50 caracteres")
        String nickname,

        @NotBlank(message = "O CEP é obrigatório")
        @Pattern(regexp = "\\d{5}-\\d{3}", message = "O CEP deve estar no formato 00000-000")
        String zipCode,

        @NotBlank(message = "A rua é obrigatória")
        @Size(max = 200, message = "A rua deve ter no máximo 200 caracteres")
        String street,

        @NotBlank(message = "O número é obrigatório")
        @Size(max = 10, message = "O número deve ter no máximo 10 caracteres")
        String streetNumber,

        @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres")
        String complement,

        @NotBlank(message = "O bairro é obrigatório")
        @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres")
        String neighborhood,

        @NotBlank(message = "A cidade é obrigatória")
        @Size(max = 100, message = "A cidade deve conter no máximo 100 caracteres")
        String city,

        @NotBlank(message = "O estado é obrigatório")
        @Size(min = 2, max = 2, message = "O estado deve conter exatamente a sigla de 2 caracteres (Ex: MG)")
        String state,

        @NotNull(message = "Defina se este é o endereço principal")
        Boolean primaryAddress) {
}
