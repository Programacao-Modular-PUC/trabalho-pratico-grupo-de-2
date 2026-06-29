package br.edu.pucminas.pm.hospedagem.service;

import br.edu.pucminas.pm.hospedagem.api.dto.AluguelRequest;
import br.edu.pucminas.pm.hospedagem.domain.Aluguel;
import br.edu.pucminas.pm.hospedagem.domain.Cliente;
import br.edu.pucminas.pm.hospedagem.domain.quarto.QuartoIndividual;
import br.edu.pucminas.pm.hospedagem.exception.DataInvalidaException;
import br.edu.pucminas.pm.hospedagem.exception.QuartoIndisponivelException;
import br.edu.pucminas.pm.hospedagem.exception.RecursoNaoPermitidoException;
import br.edu.pucminas.pm.hospedagem.repository.AluguelRepository;
import br.edu.pucminas.pm.hospedagem.repository.ClienteRepository;
import br.edu.pucminas.pm.hospedagem.repository.QuartoRepository;
import br.edu.pucminas.pm.hospedagem.service.notificacao.EventoAluguel;
import br.edu.pucminas.pm.hospedagem.service.notificacao.GerenciadorNotificacoes;
import br.edu.pucminas.pm.hospedagem.service.notificacao.TipoEventoAluguel;
import br.edu.pucminas.pm.hospedagem.domain.tarifa.TarifaRegularStrategy;
import br.edu.pucminas.pm.hospedagem.service.tarifa.TarifaStrategyResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AluguelServiceTest {

    @Mock
    private AluguelRepository aluguelRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private QuartoRepository quartoRepository;

    private AluguelService aluguelService;

    private Cliente cliente;
    private QuartoIndividual quarto;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setNome("Maria");

        quarto = new QuartoIndividual();
        quarto.setValorBase(new BigDecimal("100.00"));
        quarto.setQuantidadeCamasSolteiro(2);
        quarto.setValorAdicionalPorCama(new BigDecimal("10.00"));
        GerenciadorNotificacoes.getInstancia().limparObservadores();
        aluguelService = new AluguelService(
                aluguelRepository,
                clienteRepository,
                quartoRepository,
                new TarifaStrategyResolver(List.of(new TarifaRegularStrategy())));
    }

    @Test
    @DisplayName("Datas inválidas: fim igual ao início")
    void dataFimIgualInicio() {
        AluguelRequest req = new AluguelRequest(
                1L, 1L,
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 10),
                1, false);

        assertThrows(DataInvalidaException.class, () -> aluguelService.criar(req));
    }

    @Test
    @DisplayName("Datas inválidas: fim anterior ao início")
    void dataFimAnteriorInicio() {
        AluguelRequest req = new AluguelRequest(
                1L, 1L,
                LocalDate.of(2026, 6, 12),
                LocalDate.of(2026, 6, 10),
                1, false);

        assertThrows(DataInvalidaException.class, () -> aluguelService.criar(req));
    }

    @Test
    @DisplayName("Quarto indisponível por conflito de período")
    void quartoIndisponivel() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));
        when(aluguelRepository.findConflitosAtivos(
                eq(1L),
                eq(LocalDate.of(2026, 6, 10)),
                eq(LocalDate.of(2026, 6, 15))))
                .thenReturn(List.of(new Aluguel()));

        AluguelRequest req = new AluguelRequest(
                1L, 1L,
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 15),
                1, false);

        assertThrows(QuartoIndisponivelException.class, () -> aluguelService.criar(req));
        verify(aluguelRepository, never()).save(any());
    }

    @Test
    @DisplayName("Berço rejeitado em quarto individual")
    void bercoNaoPermitido() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));
        when(aluguelRepository.findConflitosAtivos(any(), any(), any())).thenReturn(List.of());

        AluguelRequest req = new AluguelRequest(
                1L, 1L,
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 12),
                1, true);

        assertThrows(RecursoNaoPermitidoException.class, () -> aluguelService.criar(req));
    }

    @Test
    @DisplayName("Aluguel criado com sucesso")
    void criarComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));
        when(aluguelRepository.findConflitosAtivos(any(), any(), any())).thenReturn(List.of());
        when(aluguelRepository.save(any())).thenAnswer(inv -> {
            Aluguel a = inv.getArgument(0);
            a.setCliente(cliente);
            a.setQuarto(quarto);
            return a;
        });

        AluguelRequest req = new AluguelRequest(
                1L, 1L,
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 13),
                1, false);

        var view = aluguelService.criar(req);

        assertEquals(new BigDecimal("110.00"), view.valorDiariaCalculada());
        assertEquals(new BigDecimal("330.00"), view.valorTotal());
        assertEquals(3, view.quantidadeDiarias());
        assertTrue(!view.cancelado());
    }

    @Test
    @DisplayName("Criação de aluguel notifica observers")
    void criarNotificaObservers() {
        List<EventoAluguel> eventos = new ArrayList<>();
        GerenciadorNotificacoes.getInstancia().registrar(eventos::add);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));
        when(aluguelRepository.findConflitosAtivos(any(), any(), any())).thenReturn(List.of());
        when(aluguelRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AluguelRequest req = new AluguelRequest(
                1L, 1L,
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 13),
                1, false);

        aluguelService.criar(req);

        assertEquals(1, eventos.size());
        assertEquals(TipoEventoAluguel.CRIADO, eventos.get(0).tipo());
    }

    @Test
    @DisplayName("Cancelamento de aluguel")
    void cancelarAluguel() {
        Aluguel aluguel = new Aluguel();
        aluguel.setCliente(cliente);
        aluguel.setQuarto(quarto);
        aluguel.setDataInicio(LocalDate.of(2026, 6, 10));
        aluguel.setDataFim(LocalDate.of(2026, 6, 12));
        aluguel.setNumeroHospedes(1);
        aluguel.setValorDiariaCalculada(new BigDecimal("100.00"));
        aluguel.setValorTotal(new BigDecimal("200.00"));
        aluguel.setCancelado(false);

        when(aluguelRepository.findById(5L)).thenReturn(Optional.of(aluguel));

        List<EventoAluguel> eventos = new ArrayList<>();
        GerenciadorNotificacoes.getInstancia().registrar(eventos::add);

        var view = aluguelService.cancelar(5L);

        assertTrue(view.cancelado());
        assertTrue(aluguel.isCancelado());
        assertEquals(TipoEventoAluguel.CANCELADO, eventos.get(0).tipo());
    }

    @Test
    @DisplayName("Disponibilidade: aluguel cancelado não bloqueia quarto")
    void disponibilidadeIgnoraCancelados() {
        when(aluguelRepository.findConflitosAtivos(2L, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5)))
                .thenReturn(List.of());

        aluguelService.verificarDisponibilidade(2L, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5));

        verify(aluguelRepository).findConflitosAtivos(2L, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5));
    }
}
