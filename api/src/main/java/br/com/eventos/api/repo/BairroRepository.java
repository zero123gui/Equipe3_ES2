// br/com/eventos/api/repo/BairroRepository.java
package br.com.eventos.api.repo;

import br.com.eventos.api.domain.Bairro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BairroRepository extends JpaRepository<Bairro, Integer> {
    Optional<Bairro> findByNomeBairro(String nomeBairro);
}
