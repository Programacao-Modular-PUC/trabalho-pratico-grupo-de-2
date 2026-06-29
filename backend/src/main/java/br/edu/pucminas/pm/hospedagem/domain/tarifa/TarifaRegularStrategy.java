package br.edu.pucminas.pm.hospedagem.domain.tarifa;

import br.edu.pucminas.pm.hospedagem.domain.quarto.ParametrosDiaria;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Order(Integer.MAX_VALUE)
public class TarifaRegularStrategy implements TarifaStrategy {

    @Override
    public boolean suporta(ParametrosDiaria parametros) {
        return true;
    }

    @Override
    public BigDecimal aplicar(BigDecimal valorDiariaBase, ParametrosDiaria parametros) {
        return valorDiariaBase.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String descricao() {
        return "Tarifa regular";
    }
}
