package br.edu.pucminas.pm.hospedagem.service;

import br.edu.pucminas.pm.hospedagem.api.dto.ClienteRequest;
import br.edu.pucminas.pm.hospedagem.domain.Cliente;
import br.edu.pucminas.pm.hospedagem.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
    }

    @Transactional
    public Cliente criar(ClienteRequest req) {
        Cliente c = new Cliente();
        aplicar(c, req);
        return clienteRepository.save(c);
    }

    @Transactional
    public Cliente atualizar(Long id, ClienteRequest req) {
        Cliente c = buscar(id);
        aplicar(c, req);
        return c;
    }

    @Transactional
    public void excluir(Long id) {
        clienteRepository.delete(buscar(id));
    }

    private static void aplicar(Cliente c, ClienteRequest req) {
        c.setNome(req.nome().trim());
        c.setCpf(req.cpf() != null && !req.cpf().isBlank() ? req.cpf().trim() : null);
        c.setEmail(req.email() != null && !req.email().isBlank() ? req.email().trim() : null);
    }
}
