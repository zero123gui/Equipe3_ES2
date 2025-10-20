// br/com/eventos/api/repo/LogradouroRepository.java
package br.com.eventos.api.repo;

import br.com.eventos.api.domain.Logradouro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogradouroRepository extends JpaRepository<Logradouro, Integer> {
    Optional<Logradouro> findByNomeLogradouroAndIdTipoLogradouro(String nomeLogradouro, Integer idTipoLogradouro);
}
