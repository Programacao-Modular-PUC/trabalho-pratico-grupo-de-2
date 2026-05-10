package br.edu.pucminas.pm.hospedagem.domain.quarto;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "quarto_individual")
@DiscriminatorValue("INDIVIDUAL")
@PrimaryKeyJoinColumn(name = "quarto_id")
public class QuartoIndividual extends Quarto {

    @Column(name = "quantidade_camas_solteiro", nullable = false)
    private int quantidadeCamasSolteiro;

    @Column(name = "valor_adicional_por_cama", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorAdicionalPorCama;

    @Override
    public BigDecimal calcularValorDiaria(ParametrosDiaria parametros) {
        BigDecimal extraCamas = BigDecimal.ZERO;
        if (quantidadeCamasSolteiro > 1) {
            int camasAdicionais = quantidadeCamasSolteiro - 1;
            extraCamas = valorAdicionalPorCama.multiply(BigDecimal.valueOf(camasAdicionais));
        }
        return getValorBase().add(extraCamas).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int capacidadeHospedesAdultos() {
        return quantidadeCamasSolteiro;
    }

    @Override
    public void validarParametrosAluguel(ParametrosDiaria parametros) {
        if (parametros.solicitaBerço()) {
            throw new IllegalArgumentException("Quarto individual não permite berço.");
        }
        int h = parametros.numeroHospedes();
        if (h < 1 || h > quantidadeCamasSolteiro) {
            throw new IllegalArgumentException(
                    "Número de hóspedes deve estar entre 1 e " + quantidadeCamasSolteiro + " (limite das camas).");
        }
    }

    public int getQuantidadeCamasSolteiro() {
        return quantidadeCamasSolteiro;
    }

    public void setQuantidadeCamasSolteiro(int quantidadeCamasSolteiro) {
        this.quantidadeCamasSolteiro = quantidadeCamasSolteiro;
    }

    public BigDecimal getValorAdicionalPorCama() {
        return valorAdicionalPorCama;
    }

    public void setValorAdicionalPorCama(BigDecimal valorAdicionalPorCama) {
        this.valorAdicionalPorCama = valorAdicionalPorCama;
    }
}
