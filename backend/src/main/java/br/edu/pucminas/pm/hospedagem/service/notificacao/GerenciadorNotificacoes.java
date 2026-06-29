package br.edu.pucminas.pm.hospedagem.service.notificacao;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class GerenciadorNotificacoes {

    private static final GerenciadorNotificacoes INSTANCIA = new GerenciadorNotificacoes();

    private final List<AluguelObserver> observers = new CopyOnWriteArrayList<>();

    private GerenciadorNotificacoes() {
    }

    public static GerenciadorNotificacoes getInstancia() {
        return INSTANCIA;
    }

    public void registrar(AluguelObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void remover(AluguelObserver observer) {
        observers.remove(observer);
    }

    public void limparObservadores() {
        observers.clear();
    }

    public int totalObservadores() {
        return observers.size();
    }

    public void notificar(EventoAluguel evento) {
        observers.forEach(observer -> observer.atualizar(evento));
    }
}
