package br.edu.pucminas.pm.hospedagem.api;

import br.edu.pucminas.pm.hospedagem.exception.CapacidadeExcedidaException;
import br.edu.pucminas.pm.hospedagem.exception.DataInvalidaException;
import br.edu.pucminas.pm.hospedagem.exception.QuartoIndisponivelException;
import br.edu.pucminas.pm.hospedagem.exception.RecursoNaoPermitidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(QuartoIndisponivelException.class)
    public ResponseEntity<Map<String, String>> quartoIndisponivel(QuartoIndisponivelException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler({
            CapacidadeExcedidaException.class,
            DataInvalidaException.class,
            RecursoNaoPermitidoException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Map<String, String>> requisicaoInvalida(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacao(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", msg));
    }
}
