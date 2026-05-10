package br.edu.pucminas.pm.hospedagem.repository;

import br.edu.pucminas.pm.hospedagem.domain.quarto.Quarto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    @EntityGraph(attributePaths = "residencia")
    @Override
    List<Quarto> findAll();

    @EntityGraph(attributePaths = "residencia")
    @Override
    Optional<Quarto> findById(Long id);

    List<Quarto> findByResidenciaId(Long residenciaId);
}
