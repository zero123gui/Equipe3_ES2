// br/com/eventos/api/repo/DddRepository.java
package br.com.eventos.api.repo;

import br.com.eventos.api.domain.Ddd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DddRepository extends JpaRepository<Ddd, Integer> {
    Optional<Ddd> findByNroDdd(int nroDdd);
}
