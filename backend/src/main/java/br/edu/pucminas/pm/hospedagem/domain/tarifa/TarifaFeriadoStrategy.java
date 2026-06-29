package br.edu.pucminas.pm.hospedagem.domain.tarifa;

import br.edu.pucminas.pm.hospedagem.domain.quarto.ParametrosDiaria;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Set;

@Component
@Order(10)
public class TarifaFeriadoStrategy implements TarifaStrategy {

    private static final BigDecimal FATOR_FERIADO = new BigDecimal("1.30");

    private static final Set<MonthDay> FERIADOS_NACIONAIS = Set.of(
            MonthDay.of(1, 1),
            MonthDay.of(4, 21),
            MonthDay.of(5, 1),
            MonthDay.of(9, 7),
            MonthDay.of(10, 12),
            MonthDay.of(11, 2),
            MonthDay.of(11, 15),
            MonthDay.of(12, 25)
    );

    @Override
    public boolean suporta(ParametrosDiaria parametros) {
        LocalDate dataInicio = parametros.dataInicio();
        LocalDate dataFim = parametros.dataFim();
        if (dataInicio == null || dataFim == null || !dataFim.isAfter(dataInicio)) {
            return false;
        }

        LocalDate dataAtual = dataInicio;
        while (dataAtual.isBefore(dataFim)) {
            if (FERIADOS_NACIONAIS.contains(MonthDay.from(dataAtual))) {
                return true;
            }
            dataAtual = dataAtual.plusDays(1);
        }
        return false;
    }

    @Override
    public BigDecimal aplicar(BigDecimal valorDiariaBase, ParametrosDiaria parametros) {
        return valorDiariaBase.multiply(FATOR_FERIADO).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String descricao() {
        return "Tarifa de feriado";
    }
}
