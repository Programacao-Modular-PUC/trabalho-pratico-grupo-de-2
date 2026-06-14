package br.edu.pucminas.pm.hospedagem.domain.quarto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuartoCalculoDiariaTest {

    private QuartoIndividual individual;
    private QuartoDuploCasal duploComum;
    private QuartoDuploCasal duploQueen;
    private QuartoFamilia familia;

    @BeforeEach
    void setUp() {
        individual = new QuartoIndividual();
        individual.setValorBase(new BigDecimal("100.00"));
        individual.setQuantidadeCamasSolteiro(2);
        individual.setValorAdicionalPorCama(new BigDecimal("20.00"));

        duploComum = new QuartoDuploCasal();
        duploComum.setValorBase(new BigDecimal("200.00"));
        duploComum.setTipoCamaCasal(TipoCamaCasal.COMUM);
        duploComum.setAdicionalConfortoComum(new BigDecimal("30.00"));
        duploComum.setAdicionalConfortoQueenKing(new BigDecimal("50.00"));
        duploComum.setTaxaDiariaBerço(new BigDecimal("40.00"));

        duploQueen = new QuartoDuploCasal();
        duploQueen.setValorBase(new BigDecimal("200.00"));
        duploQueen.setTipoCamaCasal(TipoCamaCasal.QUEEN_KING);
        duploQueen.setAdicionalConfortoComum(new BigDecimal("30.00"));
        duploQueen.setAdicionalConfortoQueenKing(new BigDecimal("50.00"));
        duploQueen.setTaxaDiariaBerço(new BigDecimal("40.00"));

        familia = new QuartoFamilia();
        familia.setValorBase(new BigDecimal("300.00"));
        familia.setCapacidadeMaxima(6);
        familia.setPercentualExtraMaxLotacaoCheia(new BigDecimal("0.25"));
        familia.setIncrementoDescontoPorHospedeExtra(new BigDecimal("0.02"));
        familia.setDescontoMaximoGrupo(new BigDecimal("0.10"));
    }

    @Test
    @DisplayName("Individual: valor base com uma cama")
    void individualUmaCama() {
        individual.setQuantidadeCamasSolteiro(1);
        BigDecimal valor = individual.calcularValorDiaria(new ParametrosDiaria(1, false));
        assertEquals(new BigDecimal("100.00"), valor);
    }

    @Test
    @DisplayName("Individual: adicional por cama extra")
    void individualCamasExtras() {
        BigDecimal valor = individual.calcularValorDiaria(new ParametrosDiaria(2, false));
        assertEquals(new BigDecimal("120.00"), valor);
    }

    @Test
    @DisplayName("Duplo casal: cama comum sem berço")
    void duploComumSemBerco() {
        BigDecimal valor = duploComum.calcularValorDiaria(new ParametrosDiaria(2, false));
        assertEquals(new BigDecimal("230.00"), valor);
    }

    @Test
    @DisplayName("Duplo casal: cama comum com berço")
    void duploComumComBerco() {
        BigDecimal valor = duploComum.calcularValorDiaria(new ParametrosDiaria(2, true));
        assertEquals(new BigDecimal("270.00"), valor);
    }

    @Test
    @DisplayName("Duplo casal: queen/king sem berço")
    void duploQueenSemBerco() {
        BigDecimal valor = duploQueen.calcularValorDiaria(new ParametrosDiaria(2, false));
        assertEquals(new BigDecimal("250.00"), valor);
    }

    @Test
    @DisplayName("Família: lotação parcial com desconto progressivo")
    void familiaLotacaoParcial() {
        BigDecimal valor = familia.calcularValorDiaria(new ParametrosDiaria(4, false));
        assertEquals(new BigDecimal("336.00"), valor);
    }

    @Test
    @DisplayName("Família: lotação máxima")
    void familiaLotacaoMaxima() {
        BigDecimal valor = familia.calcularValorDiaria(new ParametrosDiaria(6, false));
        assertEquals(new BigDecimal("345.00"), valor);
    }
}
