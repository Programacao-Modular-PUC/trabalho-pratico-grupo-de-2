package br.edu.pucminas.pm.hospedagem.service.notificacao;

import br.edu.pucminas.pm.hospedagem.domain.Aluguel;

public record EventoAluguel(TipoEventoAluguel tipo, Aluguel aluguel) {
}
