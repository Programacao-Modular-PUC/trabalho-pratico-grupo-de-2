package br.edu.pucminas.pm.hospedagem.repository;

import br.edu.pucminas.pm.hospedagem.domain.quarto.Quarto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT q FROM Quarto q WHERE TYPE(q) = :tipo")
    List<Quarto> findByTipo(@Param("tipo") Class<? extends Quarto> tipo);

    @Query("SELECT q FROM Quarto q WHERE q.residencia.id = :residenciaId AND TYPE(q) = :tipo")
    List<Quarto> findByResidenciaIdAndTipo(
            @Param("residenciaId") Long residenciaId,
            @Param("tipo") Class<? extends Quarto> tipo);
}
