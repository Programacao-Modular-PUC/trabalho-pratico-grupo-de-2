package br.edu.pucminas.pm.hospedagem.domain.quarto;

import java.time.LocalDate;

public record ParametrosDiaria(
        int numeroHospedes,
        boolean solicitaBerço,
        LocalDate dataInicio,
        LocalDate dataFim
) {
    public ParametrosDiaria(int numeroHospedes, boolean solicitaBerço) {
        this(numeroHospedes, solicitaBerço, null, null);
    }
}
