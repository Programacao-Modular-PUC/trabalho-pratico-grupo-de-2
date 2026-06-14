package br.edu.pucminas.pm.hospedagem.api;

import br.edu.pucminas.pm.hospedagem.api.dto.AluguelRequest;
import br.edu.pucminas.pm.hospedagem.api.view.AluguelView;
import br.edu.pucminas.pm.hospedagem.service.AluguelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alugueis")
public class AluguelController {

    private final AluguelService aluguelService;

    public AluguelController(AluguelService aluguelService) {
        this.aluguelService = aluguelService;
    }

    @GetMapping
    public List<AluguelView> listar() {
        return aluguelService.listar();
    }

    @GetMapping("/{id}")
    public AluguelView buscar(@PathVariable Long id) {
        return aluguelService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AluguelView criar(@RequestBody @Valid AluguelRequest req) {
        return aluguelService.criar(req);
    }

    @PostMapping("/{id}/cancelamento")
    public AluguelView cancelar(@PathVariable Long id) {
        return aluguelService.cancelar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        aluguelService.excluir(id);
    }
}
