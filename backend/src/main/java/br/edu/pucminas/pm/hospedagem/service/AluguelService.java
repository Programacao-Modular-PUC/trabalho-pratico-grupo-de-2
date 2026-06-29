package br.edu.pucminas.pm.hospedagem.service;

import br.edu.pucminas.pm.hospedagem.api.dto.AluguelRequest;
import br.edu.pucminas.pm.hospedagem.api.view.AluguelView;
import br.edu.pucminas.pm.hospedagem.domain.Aluguel;
import br.edu.pucminas.pm.hospedagem.domain.Cliente;
import br.edu.pucminas.pm.hospedagem.domain.quarto.ParametrosDiaria;
import br.edu.pucminas.pm.hospedagem.domain.quarto.Quarto;
import br.edu.pucminas.pm.hospedagem.exception.DataInvalidaException;
import br.edu.pucminas.pm.hospedagem.exception.QuartoIndisponivelException;
import br.edu.pucminas.pm.hospedagem.repository.AluguelRepository;
import br.edu.pucminas.pm.hospedagem.repository.ClienteRepository;
import br.edu.pucminas.pm.hospedagem.repository.QuartoRepository;
import br.edu.pucminas.pm.hospedagem.service.notificacao.EventoAluguel;
import br.edu.pucminas.pm.hospedagem.service.notificacao.GerenciadorNotificacoes;
import br.edu.pucminas.pm.hospedagem.service.notificacao.TipoEventoAluguel;
import br.edu.pucminas.pm.hospedagem.service.tarifa.TarifaStrategyResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final ClienteRepository clienteRepository;
    private final QuartoRepository quartoRepository;
    private final TarifaStrategyResolver tarifaStrategyResolver;
    private final GerenciadorNotificacoes gerenciadorNotificacoes;

    public AluguelService(
            AluguelRepository aluguelRepository,
            ClienteRepository clienteRepository,
            QuartoRepository quartoRepository,
            TarifaStrategyResolver tarifaStrategyResolver) {
        this.aluguelRepository = aluguelRepository;
        this.clienteRepository = clienteRepository;
        this.quartoRepository = quartoRepository;
        this.tarifaStrategyResolver = tarifaStrategyResolver;
        this.gerenciadorNotificacoes = GerenciadorNotificacoes.getInstancia();
    }

    @Transactional(readOnly = true)
    public List<AluguelView> listar() {
        return aluguelRepository.findAll().stream().map(this::paraView).toList();
    }

    @Transactional(readOnly = true)
    public List<AluguelView> historicoPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new IllegalArgumentException("Cliente não encontrado: " + clienteId);
        }
        return aluguelRepository.findByClienteIdOrderByDataInicioDesc(clienteId).stream()
                .map(this::paraView)
                .toList();
    }

    @Transactional(readOnly = true)
    public AluguelView buscar(Long id) {
        Aluguel a = aluguelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluguel não encontrado: " + id));
        return paraView(a);
    }

    @Transactional
    public AluguelView criar(AluguelRequest req) {
        validarDatas(req.dataInicio(), req.dataFim());

        Cliente cliente = clienteRepository.findById(req.clienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + req.clienteId()));
        Quarto quarto = quartoRepository.findById(req.quartoId())
                .orElseThrow(() -> new IllegalArgumentException("Quarto não encontrado: " + req.quartoId()));

        verificarDisponibilidade(req.quartoId(), req.dataInicio(), req.dataFim());

        long diarias = ChronoUnit.DAYS.between(req.dataInicio(), req.dataFim());

        ParametrosDiaria parametros = new ParametrosDiaria(
                req.numeroHospedes(),
                req.solicitaBerço(),
                req.dataInicio(),
                req.dataFim());
        quarto.validarParametrosAluguel(parametros);
        BigDecimal valorDiariaBase = quarto.calcularValorDiaria(parametros);
        BigDecimal valorDiaria = tarifaStrategyResolver.calcular(valorDiariaBase, parametros);
        BigDecimal valorTotal = valorDiaria.multiply(BigDecimal.valueOf(diarias)).setScale(2, RoundingMode.HALF_UP);

        Aluguel a = new Aluguel();
        a.setCliente(cliente);
        a.setQuarto(quarto);
        a.setDataInicio(req.dataInicio());
        a.setDataFim(req.dataFim());
        a.setNumeroHospedes(req.numeroHospedes());
        a.setSolicitaBerço(req.solicitaBerço());
        a.setValorDiariaCalculada(valorDiaria);
        a.setValorTotal(valorTotal);
        a.setCancelado(false);

        Aluguel salvo = aluguelRepository.save(a);
        gerenciadorNotificacoes.notificar(new EventoAluguel(TipoEventoAluguel.CRIADO, salvo));
        return paraView(salvo);
    }

    @Transactional
    public AluguelView cancelar(Long id) {
        Aluguel a = aluguelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluguel não encontrado: " + id));
        if (a.isCancelado()) {
            throw new IllegalArgumentException("Aluguel já está cancelado: " + id);
        }
        a.setCancelado(true);
        gerenciadorNotificacoes.notificar(new EventoAluguel(TipoEventoAluguel.CANCELADO, a));
        return paraView(a);
    }

    @Transactional
    public void excluir(Long id) {
        aluguelRepository.delete(
                aluguelRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Aluguel não encontrado: " + id)));
    }

    static void validarDatas(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new DataInvalidaException("Datas de início e fim são obrigatórias.");
        }
        if (!dataFim.isAfter(dataInicio)) {
            throw new DataInvalidaException("dataFim deve ser posterior a dataInicio.");
        }
        long diarias = ChronoUnit.DAYS.between(dataInicio, dataFim);
        if (diarias <= 0) {
            throw new DataInvalidaException("Estadia deve ter pelo menos 1 diária.");
        }
    }

    void verificarDisponibilidade(Long quartoId, LocalDate dataInicio, LocalDate dataFim) {
        List<Aluguel> conflitos = aluguelRepository.findConflitosAtivos(quartoId, dataInicio, dataFim);
        if (!conflitos.isEmpty()) {
            throw new QuartoIndisponivelException(
                    "Quarto #" + quartoId + " indisponível no período de "
                            + dataInicio + " a " + dataFim + ".");
        }
    }

    private AluguelView paraView(Aluguel a) {
        long diarias = ChronoUnit.DAYS.between(a.getDataInicio(), a.getDataFim());
        return AluguelView.from(a, diarias);
    }
}
