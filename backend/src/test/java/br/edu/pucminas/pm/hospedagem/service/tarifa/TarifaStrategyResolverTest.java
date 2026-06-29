package br.edu.pucminas.pm.hospedagem.service.tarifa;

import br.edu.pucminas.pm.hospedagem.domain.quarto.ParametrosDiaria;
import br.edu.pucminas.pm.hospedagem.domain.tarifa.TarifaAltaTemporadaStrategy;
import br.edu.pucminas.pm.hospedagem.domain.tarifa.TarifaFeriadoStrategy;
import br.edu.pucminas.pm.hospedagem.domain.tarifa.TarifaRegularStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TarifaStrategyResolverTest {

    private TarifaStrategyResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new TarifaStrategyResolver(List.of(
                new TarifaFeriadoStrategy(),
                new TarifaAltaTemporadaStrategy(),
                new TarifaRegularStrategy()
        ));
    }

    @Test
    @DisplayName("Tarifa regular mantém a diária base")
    void tarifaRegular() {
        BigDecimal valor = resolver.calcular(
                new BigDecimal("100.00"),
                new ParametrosDiaria(2, false, LocalDate.of(2026, 6, 10), LocalDate.of(2026, 6, 12)));

        assertEquals(new BigDecimal("100.00"), valor);
    }

    @Test
    @DisplayName("Alta temporada adiciona 20% à diária")
    void tarifaAltaTemporada() {
        BigDecimal valor = resolver.calcular(
                new BigDecimal("100.00"),
                new ParametrosDiaria(2, false, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12)));

        assertEquals(new BigDecimal("120.00"), valor);
    }

    @Test
    @DisplayName("Feriado adiciona 30% à diária")
    void tarifaFeriado() {
        BigDecimal valor = resolver.calcular(
                new BigDecimal("100.00"),
                new ParametrosDiaria(2, false, LocalDate.of(2026, 9, 7), LocalDate.of(2026, 9, 9)));

        assertEquals(new BigDecimal("130.00"), valor);
    }
}
