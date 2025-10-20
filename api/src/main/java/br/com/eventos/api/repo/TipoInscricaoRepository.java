package br.com.eventos.api.repo;

import br.com.eventos.api.domain.TipoInscricao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoInscricaoRepository extends JpaRepository<TipoInscricao, Integer> {
    Optional<TipoInscricao> findByTipo(String tipo);
    boolean existsByTipo(String tipo);
}
