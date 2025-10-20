// br/com/eventos/api/repo/UnidadeFederacaoRepository.java
package br.com.eventos.api.repo;

import br.com.eventos.api.domain.UnidadeFederacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnidadeFederacaoRepository extends JpaRepository<UnidadeFederacao, Integer> {
    Optional<UnidadeFederacao> findBySiglaUf(String siglaUf);
}
