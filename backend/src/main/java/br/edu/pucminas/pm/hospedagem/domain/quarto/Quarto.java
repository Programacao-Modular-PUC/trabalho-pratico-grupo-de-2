package br.edu.pucminas.pm.hospedagem.domain.quarto;

import br.edu.pucminas.pm.hospedagem.domain.Residencia;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "quarto")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_quarto")
public abstract class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorBase;

    @Column(name = "possui_ar", nullable = false)
    private boolean possuiAR;

    @Column(name = "possui_hidro", nullable = false)
    private boolean possuiHidro;

    @ManyToOne(optional = false)
    @JoinColumn(name = "residencia_id")
    private Residencia residencia;

    public abstract BigDecimal calcularValorDiaria(ParametrosDiaria parametros);

    public abstract int capacidadeHospedesAdultos();

    public abstract void validarParametrosAluguel(ParametrosDiaria parametros);

    public Long getId() {
        return id;
    }

    public BigDecimal getValorBase() {
        return valorBase;
    }

    public void setValorBase(BigDecimal valorBase) {
        this.valorBase = valorBase;
    }

    public boolean isPossuiAR() {
        return possuiAR;
    }

    public void setPossuiAR(boolean possuiAR) {
        this.possuiAR = possuiAR;
    }

    public boolean isPossuiHidro() {
        return possuiHidro;
    }

    public void setPossuiHidro(boolean possuiHidro) {
        this.possuiHidro = possuiHidro;
    }

    public Residencia getResidencia() {
        return residencia;
    }

    public void setResidencia(Residencia residencia) {
        this.residencia = residencia;
    }
}
