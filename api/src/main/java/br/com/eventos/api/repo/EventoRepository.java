// src/main/java/br/com/eventos/api/repo/EventoRepository.java
package br.com.eventos.api.repo;

import br.com.eventos.api.domain.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
}
