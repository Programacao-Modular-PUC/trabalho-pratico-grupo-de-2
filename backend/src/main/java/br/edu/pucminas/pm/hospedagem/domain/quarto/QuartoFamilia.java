package br.edu.pucminas.pm.hospedagem.domain.quarto;

import br.edu.pucminas.pm.hospedagem.exception.CapacidadeExcedidaException;
import br.edu.pucminas.pm.hospedagem.exception.RecursoNaoPermitidoException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "quarto_familia")
@DiscriminatorValue("FAMILIA")
@PrimaryKeyJoinColumn(name = "quarto_id")
public class QuartoFamilia extends Quarto {

    /**
     * Percentual máximo adicional sobre o valor base quando lotação = capacidade máxima.
     * Ex.: 0.25 = até 25% a mais que o base em lotação cheia (interpola linearmente por hóspedes).
     */
    @Column(name = "percentual_extra_max_lotacao_cheia", nullable = false, precision = 8, scale = 4)
    private BigDecimal percentualExtraMaxLotacaoCheia;

    @Column(name = "capacidade_maxima", nullable = false)
    private int capacidadeMaxima;

    @Column(name = "quantidade_ambientes", nullable = false)
    private int quantidadeAmbientes;

    /**
     * Desconto progressivo por pessoa a partir do 3º hóspede (além do casal base).
     * Ex.: 0.02 = +2 pontos percentuais por pessoa extra, até desconto máximo.
     */
    @Column(name = "incremento_desconto_por_hospede_extra", nullable = false, precision = 8, scale = 4)
    private BigDecimal incrementoDescontoPorHospedeExtra;

    @Column(name = "desconto_maximo_grupo", nullable = false, precision = 8, scale = 4)
    private BigDecimal descontoMaximoGrupo;

    @Override
    public BigDecimal calcularValorDiaria(ParametrosDiaria parametros) {
        int h = parametros.numeroHospedes();
        if (capacidadeMaxima <= 0) {
            throw new IllegalStateException("Capacidade máxima inválida.");
        }
        BigDecimal proporcao = BigDecimal.valueOf(h)
                .divide(BigDecimal.valueOf(capacidadeMaxima), 4, RoundingMode.HALF_UP);
        BigDecimal percentualExtra = percentualExtraMaxLotacaoCheia.multiply(proporcao);
        BigDecimal valorAntesDesconto = getValorBase().multiply(BigDecimal.ONE.add(percentualExtra));

        BigDecimal desconto = BigDecimal.ZERO;
        if (h >= 3) {
            int extras = h - 2;
            desconto = incrementoDescontoPorHospedeExtra.multiply(BigDecimal.valueOf(extras));
            if (desconto.compareTo(descontoMaximoGrupo) > 0) {
                desconto = descontoMaximoGrupo;
            }
        }

        BigDecimal fator = BigDecimal.ONE.subtract(desconto).max(BigDecimal.ZERO);
        return valorAntesDesconto.multiply(fator).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int capacidadeHospedesAdultos() {
        return capacidadeMaxima;
    }

    @Override
    public void validarParametrosAluguel(ParametrosDiaria parametros) {
        if (parametros.solicitaBerço()) {
            throw new RecursoNaoPermitidoException("Use quarto duplo casal para solicitação de berço.");
        }
        int h = parametros.numeroHospedes();
        if (h < 1 || h > capacidadeMaxima) {
            throw new CapacidadeExcedidaException(
                    "Número de hóspedes deve estar entre 1 e " + capacidadeMaxima + ".");
        }
    }

    public BigDecimal getPercentualExtraMaxLotacaoCheia() {
        return percentualExtraMaxLotacaoCheia;
    }

    public void setPercentualExtraMaxLotacaoCheia(BigDecimal percentualExtraMaxLotacaoCheia) {
        this.percentualExtraMaxLotacaoCheia = percentualExtraMaxLotacaoCheia;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public int getQuantidadeAmbientes() {
        return quantidadeAmbientes;
    }

    public void setQuantidadeAmbientes(int quantidadeAmbientes) {
        this.quantidadeAmbientes = quantidadeAmbientes;
    }

    public BigDecimal getIncrementoDescontoPorHospedeExtra() {
        return incrementoDescontoPorHospedeExtra;
    }

    public void setIncrementoDescontoPorHospedeExtra(BigDecimal incrementoDescontoPorHospedeExtra) {
        this.incrementoDescontoPorHospedeExtra = incrementoDescontoPorHospedeExtra;
    }

    public BigDecimal getDescontoMaximoGrupo() {
        return descontoMaximoGrupo;
    }

    public void setDescontoMaximoGrupo(BigDecimal descontoMaximoGrupo) {
        this.descontoMaximoGrupo = descontoMaximoGrupo;
    }
}
