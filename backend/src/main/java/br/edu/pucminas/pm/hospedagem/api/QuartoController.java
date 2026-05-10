package br.edu.pucminas.pm.hospedagem.api;

import br.edu.pucminas.pm.hospedagem.api.dto.QuartoCreateRequest;
import br.edu.pucminas.pm.hospedagem.api.view.QuartoView;
import br.edu.pucminas.pm.hospedagem.service.QuartoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/quartos")
public class QuartoController {

    private final QuartoService quartoService;

    public QuartoController(QuartoService quartoService) {
        this.quartoService = quartoService;
    }

    @GetMapping
    public List<QuartoView> listar(@RequestParam(required = false) Long residenciaId) {
        if (residenciaId != null) {
            return quartoService.listarPorResidencia(residenciaId);
        }
        return quartoService.listarTodos();
    }

    @GetMapping("/{id}")
    public QuartoView buscar(@PathVariable Long id) {
        return quartoService.buscarView(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuartoView criar(@RequestBody @Valid QuartoCreateRequest req) {
        return quartoService.criar(req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        quartoService.excluir(id);
    }
}
