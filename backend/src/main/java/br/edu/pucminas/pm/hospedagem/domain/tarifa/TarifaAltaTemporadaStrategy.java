package br.edu.pucminas.pm.hospedagem.domain.tarifa;

import br.edu.pucminas.pm.hospedagem.domain.quarto.ParametrosDiaria;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;

@Component
@Order(20)
public class TarifaAltaTemporadaStrategy implements TarifaStrategy {

    private static final BigDecimal FATOR_ALTA_TEMPORADA = new BigDecimal("1.20");

    @Override
    public boolean suporta(ParametrosDiaria parametros) {
        LocalDate dataInicio = parametros.dataInicio();
        return dataInicio != null
                && (dataInicio.getMonth() == Month.JANUARY
                || dataInicio.getMonth() == Month.JULY
                || dataInicio.getMonth() == Month.DECEMBER);
    }

    @Override
    public BigDecimal aplicar(BigDecimal valorDiariaBase, ParametrosDiaria parametros) {
        return valorDiariaBase.multiply(FATOR_ALTA_TEMPORADA).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String descricao() {
        return "Tarifa de alta temporada";
    }
}
