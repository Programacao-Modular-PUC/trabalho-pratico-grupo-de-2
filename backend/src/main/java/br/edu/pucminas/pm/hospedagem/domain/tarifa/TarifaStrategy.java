package br.edu.pucminas.pm.hospedagem.domain.tarifa;

import br.edu.pucminas.pm.hospedagem.domain.quarto.ParametrosDiaria;

import java.math.BigDecimal;

public interface TarifaStrategy {

    boolean suporta(ParametrosDiaria parametros);

    BigDecimal aplicar(BigDecimal valorDiariaBase, ParametrosDiaria parametros);

    String descricao();
}
