package br.edu.pucminas.pm.hospedagem.domain;

import br.edu.pucminas.pm.hospedagem.domain.quarto.Quarto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "aluguel")
public class Aluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "quarto_id")
    private Quarto quarto;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "numero_hospedes", nullable = false)
    private int numeroHospedes;

    @Column(name = "solicita_berco", nullable = false)
    private boolean solicitaBerço;

    @Column(name = "valor_diaria_calculada", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorDiariaCalculada;

    @Column(name = "valor_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorTotal;

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public int getNumeroHospedes() {
        return numeroHospedes;
    }

    public void setNumeroHospedes(int numeroHospedes) {
        this.numeroHospedes = numeroHospedes;
    }

    public boolean isSolicitaBerço() {
        return solicitaBerço;
    }

    public void setSolicitaBerço(boolean solicitaBerço) {
        this.solicitaBerço = solicitaBerço;
    }

    public BigDecimal getValorDiariaCalculada() {
        return valorDiariaCalculada;
    }

    public void setValorDiariaCalculada(BigDecimal valorDiariaCalculada) {
        this.valorDiariaCalculada = valorDiariaCalculada;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
