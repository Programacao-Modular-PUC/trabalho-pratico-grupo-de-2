package br.edu.pucminas.pm.hospedagem.service;

import br.edu.pucminas.pm.hospedagem.exception.DataInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AluguelServiceValidacaoDatasTest {

    @Test
    @DisplayName("Datas válidas com pelo menos uma diária")
    void datasValidas() {
        assertDoesNotThrow(() -> AluguelService.validarDatas(
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 11)));
    }

    @Test
    @DisplayName("Data nula")
    void dataNula() {
        assertThrows(DataInvalidaException.class,
                () -> AluguelService.validarDatas(null, LocalDate.of(2026, 6, 11)));
    }
}
