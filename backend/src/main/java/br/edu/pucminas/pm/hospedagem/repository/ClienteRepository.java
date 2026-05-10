package br.edu.pucminas.pm.hospedagem.repository;

import br.edu.pucminas.pm.hospedagem.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
