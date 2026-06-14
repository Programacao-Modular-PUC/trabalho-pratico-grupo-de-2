package br.edu.pucminas.pm.hospedagem.domain.quarto;

import br.edu.pucminas.pm.hospedagem.exception.CapacidadeExcedidaException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "quarto_duplo_casal")
@DiscriminatorValue("DUPLO_CASAL")
@PrimaryKeyJoinColumn(name = "quarto_id")
public class QuartoDuploCasal extends Quarto {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cama_casal", nullable = false, length = 20)
    private TipoCamaCasal tipoCamaCasal;

    @Column(name = "adicional_conforto_comum", nullable = false, precision = 12, scale = 2)
    private BigDecimal adicionalConfortoComum;

    @Column(name = "adicional_conforto_queen_king", nullable = false, precision = 12, scale = 2)
    private BigDecimal adicionalConfortoQueenKing;

    @Column(name = "taxa_diaria_berco", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxaDiariaBerço;

    @Override
    public BigDecimal calcularValorDiaria(ParametrosDiaria parametros) {
        BigDecimal conforto = tipoCamaCasal == TipoCamaCasal.COMUM
                ? adicionalConfortoComum
                : adicionalConfortoQueenKing;
        BigDecimal total = getValorBase().add(conforto);
        if (parametros.solicitaBerço()) {
            total = total.add(taxaDiariaBerço);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int capacidadeHospedesAdultos() {
        return 2;
    }

    @Override
    public void validarParametrosAluguel(ParametrosDiaria parametros) {
        int h = parametros.numeroHospedes();
        if (h < 1 || h > 2) {
            throw new CapacidadeExcedidaException("Quarto duplo casal comporta até 2 hóspedes adultos.");
        }
    }

    public TipoCamaCasal getTipoCamaCasal() {
        return tipoCamaCasal;
    }

    public void setTipoCamaCasal(TipoCamaCasal tipoCamaCasal) {
        this.tipoCamaCasal = tipoCamaCasal;
    }

    public BigDecimal getAdicionalConfortoComum() {
        return adicionalConfortoComum;
    }

    public void setAdicionalConfortoComum(BigDecimal adicionalConfortoComum) {
        this.adicionalConfortoComum = adicionalConfortoComum;
    }

    public BigDecimal getAdicionalConfortoQueenKing() {
        return adicionalConfortoQueenKing;
    }

    public void setAdicionalConfortoQueenKing(BigDecimal adicionalConfortoQueenKing) {
        this.adicionalConfortoQueenKing = adicionalConfortoQueenKing;
    }

    public BigDecimal getTaxaDiariaBerço() {
        return taxaDiariaBerço;
    }

    public void setTaxaDiariaBerço(BigDecimal taxaDiariaBerço) {
        this.taxaDiariaBerço = taxaDiariaBerço;
    }
}
