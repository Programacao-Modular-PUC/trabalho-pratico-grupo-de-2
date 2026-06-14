package br.edu.pucminas.pm.hospedagem.repository;

import br.edu.pucminas.pm.hospedagem.domain.Aluguel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AluguelRepository extends JpaRepository<Aluguel, Long> {

    @EntityGraph(attributePaths = {"cliente", "quarto", "quarto.residencia"})
    @Override
    List<Aluguel> findAll();

    @EntityGraph(attributePaths = {"cliente", "quarto", "quarto.residencia"})
    @Override
    Optional<Aluguel> findById(Long id);

    @EntityGraph(attributePaths = {"cliente", "quarto", "quarto.residencia"})
    List<Aluguel> findByClienteIdOrderByDataInicioDesc(Long clienteId);

    @Query("""
            SELECT a FROM Aluguel a
            WHERE a.quarto.id = :quartoId
            AND a.cancelado = false
            AND a.dataInicio < :dataFim
            AND a.dataFim > :dataInicio
            """)
    List<Aluguel> findConflitosAtivos(
            @Param("quartoId") Long quartoId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim);
}
