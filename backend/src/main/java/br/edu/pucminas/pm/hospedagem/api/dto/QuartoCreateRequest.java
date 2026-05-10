package br.edu.pucminas.pm.hospedagem.api.dto;

import br.edu.pucminas.pm.hospedagem.domain.quarto.TipoCamaCasal;
import br.edu.pucminas.pm.hospedagem.domain.quarto.TipoQuarto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record QuartoCreateRequest(
        @NotNull TipoQuarto tipo,
        @NotNull Long residenciaId,
        @NotNull @DecimalMin("0.0") BigDecimal valorBase,
        boolean possuiAR,
        boolean possuiHidro,

        @Min(1) Integer quantidadeCamasSolteiro,
        @DecimalMin("0.0") BigDecimal valorAdicionalPorCama,

        TipoCamaCasal tipoCamaCasal,
        @DecimalMin("0.0") BigDecimal adicionalConfortoComum,
        @DecimalMin("0.0") BigDecimal adicionalConfortoQueenKing,
        @DecimalMin("0.0") BigDecimal taxaDiariaBerço,

        @Min(1) Integer capacidadeMaxima,
        @Min(0) Integer quantidadeAmbientes,
        @DecimalMin("0.0") BigDecimal percentualExtraMaxLotacaoCheia,
        @DecimalMin("0.0") BigDecimal incrementoDescontoPorHospedeExtra,
        @DecimalMin("0.0") BigDecimal descontoMaximoGrupo
) {
}
