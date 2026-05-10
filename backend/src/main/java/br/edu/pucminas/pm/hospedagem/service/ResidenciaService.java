package br.edu.pucminas.pm.hospedagem.service;

import br.edu.pucminas.pm.hospedagem.api.dto.ResidenciaRequest;
import br.edu.pucminas.pm.hospedagem.domain.Residencia;
import br.edu.pucminas.pm.hospedagem.repository.ResidenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResidenciaService {

    private final ResidenciaRepository residenciaRepository;

    public ResidenciaService(ResidenciaRepository residenciaRepository) {
        this.residenciaRepository = residenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<Residencia> listar() {
        return residenciaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Residencia buscar(Long id) {
        return residenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Residência não encontrada: " + id));
    }

    @Transactional
    public Residencia criar(ResidenciaRequest req) {
        Residencia r = new Residencia();
        r.setNome(req.nome().trim());
        r.setEndereco(req.endereco() != null ? req.endereco().trim() : null);
        return residenciaRepository.save(r);
    }

    @Transactional
    public Residencia atualizar(Long id, ResidenciaRequest req) {
        Residencia r = buscar(id);
        r.setNome(req.nome().trim());
        r.setEndereco(req.endereco() != null ? req.endereco().trim() : null);
        return r;
    }

    @Transactional
    public void excluir(Long id) {
        residenciaRepository.delete(buscar(id));
    }
}
