package br.edu.pucminas.pm.hospedagem.service;

import br.edu.pucminas.pm.hospedagem.api.dto.AluguelRequest;
import br.edu.pucminas.pm.hospedagem.api.view.AluguelView;
import br.edu.pucminas.pm.hospedagem.domain.Aluguel;
import br.edu.pucminas.pm.hospedagem.domain.Cliente;
import br.edu.pucminas.pm.hospedagem.domain.quarto.ParametrosDiaria;
import br.edu.pucminas.pm.hospedagem.domain.quarto.Quarto;
import br.edu.pucminas.pm.hospedagem.repository.AluguelRepository;
import br.edu.pucminas.pm.hospedagem.repository.ClienteRepository;
import br.edu.pucminas.pm.hospedagem.repository.QuartoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final ClienteRepository clienteRepository;
    private final QuartoRepository quartoRepository;

    public AluguelService(
            AluguelRepository aluguelRepository,
            ClienteRepository clienteRepository,
            QuartoRepository quartoRepository) {
        this.aluguelRepository = aluguelRepository;
        this.clienteRepository = clienteRepository;
        this.quartoRepository = quartoRepository;
    }

    @Transactional(readOnly = true)
    public List<AluguelView> listar() {
        return aluguelRepository.findAll().stream().map(this::paraView).toList();
    }

    @Transactional(readOnly = true)
    public AluguelView buscar(Long id) {
        Aluguel a = aluguelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluguel não encontrado: " + id));
        return paraView(a);
    }

    @Transactional
    public AluguelView criar(AluguelRequest req) {
        Cliente cliente = clienteRepository.findById(req.clienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + req.clienteId()));
        Quarto quarto = quartoRepository.findById(req.quartoId())
                .orElseThrow(() -> new IllegalArgumentException("Quarto não encontrado: " + req.quartoId()));

        if (!req.dataFim().isAfter(req.dataInicio())) {
            throw new IllegalArgumentException("dataFim deve ser posterior a dataInicio.");
        }

        long diarias = ChronoUnit.DAYS.between(req.dataInicio(), req.dataFim());
        if (diarias <= 0) {
            throw new IllegalArgumentException("Estadia deve ter pelo menos 1 diária.");
        }

        ParametrosDiaria parametros = new ParametrosDiaria(req.numeroHospedes(), req.solicitaBerço());
        quarto.validarParametrosAluguel(parametros);
        BigDecimal valorDiaria = quarto.calcularValorDiaria(parametros);
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

        return paraView(aluguelRepository.save(a));
    }

    @Transactional
    public void excluir(Long id) {
        aluguelRepository.delete(
                aluguelRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Aluguel não encontrado: " + id)));
    }

    private AluguelView paraView(Aluguel a) {
        long diarias = ChronoUnit.DAYS.between(a.getDataInicio(), a.getDataFim());
        return AluguelView.from(a, diarias);
    }
}
