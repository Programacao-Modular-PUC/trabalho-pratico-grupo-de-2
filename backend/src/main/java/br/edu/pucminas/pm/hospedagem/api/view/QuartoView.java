package br.edu.pucminas.pm.hospedagem.api.view;

import br.edu.pucminas.pm.hospedagem.domain.quarto.Quarto;
import br.edu.pucminas.pm.hospedagem.domain.quarto.QuartoDuploCasal;
import br.edu.pucminas.pm.hospedagem.domain.quarto.QuartoFamilia;
import br.edu.pucminas.pm.hospedagem.domain.quarto.QuartoIndividual;

import java.math.BigDecimal;

public record QuartoView(
        Long id,
        String tipo,
        BigDecimal valorBase,
        boolean possuiAR,
        boolean possuiHidro,
        Long residenciaId,
        DetalhesIndividual individual,
        DetalhesDuplo duplo,
        DetalhesFamilia familia
) {

    public record DetalhesIndividual(Integer quantidadeCamasSolteiro, BigDecimal valorAdicionalPorCama) {
    }

    public record DetalhesDuplo(
            String tipoCamaCasal,
            BigDecimal adicionalConfortoComum,
            BigDecimal adicionalConfortoQueenKing,
            BigDecimal taxaDiariaBerço
    ) {
    }

    public record DetalhesFamilia(
            Integer capacidadeMaxima,
            Integer quantidadeAmbientes,
            BigDecimal percentualExtraMaxLotacaoCheia,
            BigDecimal incrementoDescontoPorHospedeExtra,
            BigDecimal descontoMaximoGrupo
    ) {
    }

    public static QuartoView from(Quarto q) {
        Long rid = q.getResidencia() != null ? q.getResidencia().getId() : null;
        if (q instanceof QuartoIndividual qi) {
            return new QuartoView(
                    q.getId(),
                    "INDIVIDUAL",
                    q.getValorBase(),
                    q.isPossuiAR(),
                    q.isPossuiHidro(),
                    rid,
                    new DetalhesIndividual(qi.getQuantidadeCamasSolteiro(), qi.getValorAdicionalPorCama()),
                    null,
                    null
            );
        }
        if (q instanceof QuartoDuploCasal qd) {
            return new QuartoView(
                    q.getId(),
                    "DUPLO_CASAL",
                    q.getValorBase(),
                    q.isPossuiAR(),
                    q.isPossuiHidro(),
                    rid,
                    null,
                    new DetalhesDuplo(
                            qd.getTipoCamaCasal().name(),
                            qd.getAdicionalConfortoComum(),
                            qd.getAdicionalConfortoQueenKing(),
                            qd.getTaxaDiariaBerço()
                    ),
                    null
            );
        }
        if (q instanceof QuartoFamilia qf) {
            return new QuartoView(
                    q.getId(),
                    "FAMILIA",
                    q.getValorBase(),
                    q.isPossuiAR(),
                    q.isPossuiHidro(),
                    rid,
                    null,
                    null,
                    new DetalhesFamilia(
                            qf.getCapacidadeMaxima(),
                            qf.getQuantidadeAmbientes(),
                            qf.getPercentualExtraMaxLotacaoCheia(),
                            qf.getIncrementoDescontoPorHospedeExtra(),
                            qf.getDescontoMaximoGrupo()
                    )
            );
        }
        throw new IllegalArgumentException("Tipo de quarto desconhecido: " + q.getClass());
    }
}
