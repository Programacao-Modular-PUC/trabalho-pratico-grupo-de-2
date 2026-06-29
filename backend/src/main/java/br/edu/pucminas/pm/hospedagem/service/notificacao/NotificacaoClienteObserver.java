package br.edu.pucminas.pm.hospedagem.service.notificacao;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoClienteObserver implements AluguelObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificacaoClienteObserver.class);

    @PostConstruct
    void registrarObserver() {
        GerenciadorNotificacoes.getInstancia().registrar(this);
    }

    @Override
    public void atualizar(EventoAluguel evento) {
        if (evento.tipo() == TipoEventoAluguel.CRIADO) {
            LOGGER.info("Notificação interna: reserva criada para o cliente {}.",
                    evento.aluguel().getCliente().getNome());
        }
        if (evento.tipo() == TipoEventoAluguel.CANCELADO) {
            LOGGER.info("Notificação interna: reserva cancelada para o cliente {}.",
                    evento.aluguel().getCliente().getNome());
        }
    }
}
