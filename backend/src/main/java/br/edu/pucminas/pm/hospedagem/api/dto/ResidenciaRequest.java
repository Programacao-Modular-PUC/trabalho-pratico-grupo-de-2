package br.edu.pucminas.pm.hospedagem.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ResidenciaRequest(
        @NotBlank String nome,
        String endereco
) {
}
