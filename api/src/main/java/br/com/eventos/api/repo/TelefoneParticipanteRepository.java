package br.com.eventos.api.repo;

import br.com.eventos.api.domain.TelefoneParticipante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TelefoneParticipanteRepository extends JpaRepository<TelefoneParticipante, Integer> {
    List<TelefoneParticipante> findByIdParticipante(Integer idParticipante);
}
