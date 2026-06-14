package br.edu.pucminas.pm.hospedagem.api;

import br.edu.pucminas.pm.hospedagem.api.dto.ClienteRequest;
import br.edu.pucminas.pm.hospedagem.api.view.AluguelView;
import br.edu.pucminas.pm.hospedagem.domain.Cliente;
import br.edu.pucminas.pm.hospedagem.service.AluguelService;
import br.edu.pucminas.pm.hospedagem.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final AluguelService aluguelService;

    public ClienteController(ClienteService clienteService, AluguelService aluguelService) {
        this.clienteService = clienteService;
        this.aluguelService = aluguelService;
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public Cliente buscar(@PathVariable Long id) {
        return clienteService.buscar(id);
    }

    @GetMapping("/{id}/alugueis")
    public List<AluguelView> historicoAlugueis(@PathVariable Long id) {
        return aluguelService.historicoPorCliente(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente criar(@RequestBody @Valid ClienteRequest req) {
        return clienteService.criar(req);
    }

    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Long id, @RequestBody @Valid ClienteRequest req) {
        return clienteService.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        clienteService.excluir(id);
    }
}
