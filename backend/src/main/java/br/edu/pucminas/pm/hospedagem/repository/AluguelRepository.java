package br.edu.pucminas.pm.hospedagem.repository;

import br.edu.pucminas.pm.hospedagem.domain.Aluguel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AluguelRepository extends JpaRepository<Aluguel, Long> {

    @EntityGraph(attributePaths = {"cliente", "quarto", "quarto.residencia"})
    @Override
    List<Aluguel> findAll();

    @EntityGraph(attributePaths = {"cliente", "quarto", "quarto.residencia"})
    @Override
    Optional<Aluguel> findById(Long id);
}
