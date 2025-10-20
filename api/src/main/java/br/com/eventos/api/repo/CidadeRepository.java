// br/com/eventos/api/repo/CidadeRepository.java
package br.com.eventos.api.repo;

import br.com.eventos.api.domain.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CidadeRepository extends JpaRepository<Cidade, Integer> {
    Optional<Cidade> findByNomeCidadeAndIdUf(String nomeCidade, Integer idUf);
}
