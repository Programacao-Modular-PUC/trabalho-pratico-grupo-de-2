package br.edu.pucminas.pm.hospedagem.service.notificacao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class GerenciadorNotificacoesTest {

    @Test
    @DisplayName("Gerenciador de notificações usa uma única instância")
    void singletonMantemInstanciaUnica() {
        GerenciadorNotificacoes primeiraInstancia = GerenciadorNotificacoes.getInstancia();
        GerenciadorNotificacoes segundaInstancia = GerenciadorNotificacoes.getInstancia();

        assertSame(primeiraInstancia, segundaInstancia);
    }
}
