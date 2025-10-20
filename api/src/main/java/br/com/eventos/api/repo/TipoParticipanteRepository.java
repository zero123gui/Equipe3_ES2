package br.com.eventos.api.repo;

import br.com.eventos.api.domain.TipoParticipante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoParticipanteRepository extends JpaRepository<TipoParticipante, Integer> {
    Optional<TipoParticipante> findByTipo(String tipo);
    boolean existsByTipo(String tipo);
}
