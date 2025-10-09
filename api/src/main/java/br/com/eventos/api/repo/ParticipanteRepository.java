package br.com.eventos.api.repo;

import br.com.eventos.api.domain.Participante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipanteRepository extends JpaRepository<Participante, Integer> {}
