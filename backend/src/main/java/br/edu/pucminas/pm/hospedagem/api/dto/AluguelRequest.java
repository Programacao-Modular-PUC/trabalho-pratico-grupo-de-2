package br.edu.pucminas.pm.hospedagem.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AluguelRequest(
        @NotNull Long clienteId,
        @NotNull Long quartoId,
        @NotNull LocalDate dataInicio,
        @NotNull LocalDate dataFim,
        @NotNull @Min(1) Integer numeroHospedes,
        boolean solicitaBerço
) {
}
