package br.edu.pucminas.pm.hospedagem.domain.quarto;

import br.edu.pucminas.pm.hospedagem.exception.CapacidadeExcedidaException;
import br.edu.pucminas.pm.hospedagem.exception.RecursoNaoPermitidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuartoValidacaoTest {

    private QuartoIndividual individual;
    private QuartoDuploCasal duplo;
    private QuartoFamilia familia;

    @BeforeEach
    void setUp() {
        individual = new QuartoIndividual();
        individual.setQuantidadeCamasSolteiro(2);

        duplo = new QuartoDuploCasal();

        familia = new QuartoFamilia();
        familia.setCapacidadeMaxima(5);
    }

    @Test
    @DisplayName("Individual: berço não permitido")
    void individualRejeitaBerco() {
        assertThrows(RecursoNaoPermitidoException.class,
                () -> individual.validarParametrosAluguel(new ParametrosDiaria(1, true)));
    }

    @Test
    @DisplayName("Individual: capacidade dentro do limite")
    void individualCapacidadeOk() {
        assertDoesNotThrow(() -> individual.validarParametrosAluguel(new ParametrosDiaria(2, false)));
    }

    @Test
    @DisplayName("Individual: capacidade excedida")
    void individualCapacidadeExcedida() {
        assertThrows(CapacidadeExcedidaException.class,
                () -> individual.validarParametrosAluguel(new ParametrosDiaria(3, false)));
    }

    @Test
    @DisplayName("Individual: zero hóspedes")
    void individualZeroHospedes() {
        assertThrows(CapacidadeExcedidaException.class,
                () -> individual.validarParametrosAluguel(new ParametrosDiaria(0, false)));
    }

    @Test
    @DisplayName("Duplo casal: berço permitido")
    void duploPermiteBerco() {
        assertDoesNotThrow(() -> duplo.validarParametrosAluguel(new ParametrosDiaria(2, true)));
    }

    @Test
    @DisplayName("Duplo casal: mais de 2 hóspedes")
    void duploCapacidadeExcedida() {
        assertThrows(CapacidadeExcedidaException.class,
                () -> duplo.validarParametrosAluguel(new ParametrosDiaria(3, false)));
    }

    @Test
    @DisplayName("Família: berço não permitido")
    void familiaRejeitaBerco() {
        assertThrows(RecursoNaoPermitidoException.class,
                () -> familia.validarParametrosAluguel(new ParametrosDiaria(3, true)));
    }

    @Test
    @DisplayName("Família: capacidade excedida")
    void familiaCapacidadeExcedida() {
        assertThrows(CapacidadeExcedidaException.class,
                () -> familia.validarParametrosAluguel(new ParametrosDiaria(6, false)));
    }
}
