package br.edu.pucminas.pm.hospedagem.service.tarifa;

import br.edu.pucminas.pm.hospedagem.domain.quarto.ParametrosDiaria;
import br.edu.pucminas.pm.hospedagem.domain.tarifa.TarifaStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TarifaStrategyResolver {

    private final List<TarifaStrategy> strategies;

    public TarifaStrategyResolver(List<TarifaStrategy> strategies) {
        this.strategies = strategies;
    }

    public TarifaStrategy resolver(ParametrosDiaria parametros) {
        return strategies.stream()
                .filter(strategy -> strategy.suporta(parametros))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhuma estratégia de tarifa disponível."));
    }

    public BigDecimal calcular(BigDecimal valorDiariaBase, ParametrosDiaria parametros) {
        return resolver(parametros).aplicar(valorDiariaBase, parametros);
    }
}
