// br/com/eventos/api/repo/TipoLogradouroRepository.java
package br.com.eventos.api.repo;

import br.com.eventos.api.domain.TipoLogradouro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoLogradouroRepository extends JpaRepository<TipoLogradouro, Integer> {
    Optional<TipoLogradouro> findBySiglaLogradouro(String sigla);
}
