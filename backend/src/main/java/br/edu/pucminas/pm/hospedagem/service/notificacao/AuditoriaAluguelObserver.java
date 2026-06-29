package br.edu.pucminas.pm.hospedagem.service.notificacao;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaAluguelObserver implements AluguelObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditoriaAluguelObserver.class);

    @PostConstruct
    void registrarObserver() {
        GerenciadorNotificacoes.getInstancia().registrar(this);
    }

    @Override
    public void atualizar(EventoAluguel evento) {
        LOGGER.info("Auditoria de aluguel: evento {} para cliente {} e quarto {}.",
                evento.tipo(),
                evento.aluguel().getCliente().getNome(),
                evento.aluguel().getQuarto().getId());
    }
}
