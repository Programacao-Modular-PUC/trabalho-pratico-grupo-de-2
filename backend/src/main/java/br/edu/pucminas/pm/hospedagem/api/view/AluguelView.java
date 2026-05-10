package br.edu.pucminas.pm.hospedagem.api.view;

import br.edu.pucminas.pm.hospedagem.domain.Aluguel;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AluguelView(
        Long id,
        Long clienteId,
        String clienteNome,
        Long quartoId,
        QuartoView quarto,
        LocalDate dataInicio,
        LocalDate dataFim,
        int numeroHospedes,
        boolean solicitaBerço,
        BigDecimal valorDiariaCalculada,
        BigDecimal valorTotal,
        long quantidadeDiarias
) {
    public static AluguelView from(Aluguel a, long quantidadeDiarias) {
        return new AluguelView(
                a.getId(),
                a.getCliente().getId(),
                a.getCliente().getNome(),
                a.getQuarto().getId(),
                QuartoView.from(a.getQuarto()),
                a.getDataInicio(),
                a.getDataFim(),
                a.getNumeroHospedes(),
                a.isSolicitaBerço(),
                a.getValorDiariaCalculada(),
                a.getValorTotal(),
                quantidadeDiarias
        );
    }
}
