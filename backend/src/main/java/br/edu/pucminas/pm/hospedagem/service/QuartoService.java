package br.edu.pucminas.pm.hospedagem.service;

import br.edu.pucminas.pm.hospedagem.api.dto.QuartoCreateRequest;
import br.edu.pucminas.pm.hospedagem.api.view.QuartoView;
import br.edu.pucminas.pm.hospedagem.domain.Residencia;
import br.edu.pucminas.pm.hospedagem.domain.quarto.Quarto;
import br.edu.pucminas.pm.hospedagem.domain.quarto.QuartoDuploCasal;
import br.edu.pucminas.pm.hospedagem.domain.quarto.QuartoFamilia;
import br.edu.pucminas.pm.hospedagem.domain.quarto.QuartoIndividual;
import br.edu.pucminas.pm.hospedagem.domain.quarto.TipoQuarto;
import br.edu.pucminas.pm.hospedagem.repository.QuartoRepository;
import br.edu.pucminas.pm.hospedagem.repository.ResidenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class QuartoService {

    private final QuartoRepository quartoRepository;
    private final ResidenciaRepository residenciaRepository;

    public QuartoService(QuartoRepository quartoRepository, ResidenciaRepository residenciaRepository) {
        this.quartoRepository = quartoRepository;
        this.residenciaRepository = residenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<QuartoView> listarTodos() {
        return quartoRepository.findAll().stream().map(QuartoView::from).toList();
    }

    @Transactional(readOnly = true)
    public List<QuartoView> listarPorResidencia(Long residenciaId) {
        return quartoRepository.findByResidenciaId(residenciaId).stream().map(QuartoView::from).toList();
    }

    @Transactional(readOnly = true)
    public QuartoView buscarView(Long id) {
        return QuartoView.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Quarto buscarEntidade(Long id) {
        return quartoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quarto não encontrado: " + id));
    }

    @Transactional
    public QuartoView criar(QuartoCreateRequest req) {
        Residencia residencia = residenciaRepository.findById(req.residenciaId())
                .orElseThrow(() -> new IllegalArgumentException("Residência não encontrada: " + req.residenciaId()));

        Quarto quarto = switch (req.tipo()) {
            case INDIVIDUAL -> montarIndividual(req, residencia);
            case DUPLO_CASAL -> montarDuplo(req, residencia);
            case FAMILIA -> montarFamilia(req, residencia);
        };

        return QuartoView.from(quartoRepository.save(quarto));
    }

    @Transactional
    public void excluir(Long id) {
        quartoRepository.delete(buscarEntidade(id));
    }

    private static QuartoIndividual montarIndividual(QuartoCreateRequest req, Residencia residencia) {
        if (req.quantidadeCamasSolteiro() == null || req.valorAdicionalPorCama() == null) {
            throw new IllegalArgumentException("Quarto individual exige quantidadeCamasSolteiro e valorAdicionalPorCama.");
        }
        if (req.quantidadeCamasSolteiro() < 1) {
            throw new IllegalArgumentException("Deve haver pelo menos 1 cama de solteiro.");
        }
        QuartoIndividual q = new QuartoIndividual();
        comum(req, residencia, q);
        q.setQuantidadeCamasSolteiro(req.quantidadeCamasSolteiro());
        q.setValorAdicionalPorCama(req.valorAdicionalPorCama());
        return q;
    }

    private static QuartoDuploCasal montarDuplo(QuartoCreateRequest req, Residencia residencia) {
        if (req.tipoCamaCasal() == null || req.adicionalConfortoComum() == null
                || req.adicionalConfortoQueenKing() == null || req.taxaDiariaBerço() == null) {
            throw new IllegalArgumentException(
                    "Quarto duplo casal exige tipoCamaCasal, adicionalConfortoComum, adicionalConfortoQueenKing e taxaDiariaBerço.");
        }
        QuartoDuploCasal q = new QuartoDuploCasal();
        comum(req, residencia, q);
        q.setTipoCamaCasal(req.tipoCamaCasal());
        q.setAdicionalConfortoComum(req.adicionalConfortoComum());
        q.setAdicionalConfortoQueenKing(req.adicionalConfortoQueenKing());
        q.setTaxaDiariaBerço(req.taxaDiariaBerço());
        return q;
    }

    private static QuartoFamilia montarFamilia(QuartoCreateRequest req, Residencia residencia) {
        if (req.capacidadeMaxima() == null || req.quantidadeAmbientes() == null
                || req.percentualExtraMaxLotacaoCheia() == null
                || req.incrementoDescontoPorHospedeExtra() == null
                || req.descontoMaximoGrupo() == null) {
            throw new IllegalArgumentException(
                    "Quarto família exige capacidadeMaxima, quantidadeAmbientes, percentualExtraMaxLotacaoCheia, incrementoDescontoPorHospedeExtra e descontoMaximoGrupo.");
        }
        if (req.descontoMaximoGrupo().compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("descontoMaximoGrupo não pode ser maior que 1.");
        }
        QuartoFamilia q = new QuartoFamilia();
        comum(req, residencia, q);
        q.setCapacidadeMaxima(req.capacidadeMaxima());
        q.setQuantidadeAmbientes(req.quantidadeAmbientes());
        q.setPercentualExtraMaxLotacaoCheia(req.percentualExtraMaxLotacaoCheia());
        q.setIncrementoDescontoPorHospedeExtra(req.incrementoDescontoPorHospedeExtra());
        q.setDescontoMaximoGrupo(req.descontoMaximoGrupo());
        return q;
    }

    private static void comum(QuartoCreateRequest req, Residencia residencia, Quarto q) {
        q.setValorBase(req.valorBase());
        q.setPossuiAR(req.possuiAR());
        q.setPossuiHidro(req.possuiHidro());
        q.setResidencia(residencia);
    }
}
