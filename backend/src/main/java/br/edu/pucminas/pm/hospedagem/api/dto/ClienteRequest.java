package br.edu.pucminas.pm.hospedagem.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
        @NotBlank String nome,
        String cpf,
        String email
) {
}
