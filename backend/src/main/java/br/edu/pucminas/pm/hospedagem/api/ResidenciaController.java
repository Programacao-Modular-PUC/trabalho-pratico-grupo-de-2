package br.edu.pucminas.pm.hospedagem.api;

import br.edu.pucminas.pm.hospedagem.api.dto.ResidenciaRequest;
import br.edu.pucminas.pm.hospedagem.domain.Residencia;
import br.edu.pucminas.pm.hospedagem.service.ResidenciaService;
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
@RequestMapping("/residencias")
public class ResidenciaController {

    private final ResidenciaService residenciaService;

    public ResidenciaController(ResidenciaService residenciaService) {
        this.residenciaService = residenciaService;
    }

    @GetMapping
    public List<Residencia> listar() {
        return residenciaService.listar();
    }

    @GetMapping("/{id}")
    public Residencia buscar(@PathVariable Long id) {
        return residenciaService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Residencia criar(@RequestBody @Valid ResidenciaRequest req) {
        return residenciaService.criar(req);
    }

    @PutMapping("/{id}")
    public Residencia atualizar(@PathVariable Long id, @RequestBody @Valid ResidenciaRequest req) {
        return residenciaService.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        residenciaService.excluir(id);
    }
}
