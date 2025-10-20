package br.com.eventos.api.repo;

import br.com.eventos.api.domain.ParticipanteEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipanteEventoRepository extends JpaRepository<ParticipanteEvento, Integer> {
    Page<ParticipanteEvento> findByIdEvento(Integer idEvento, Pageable pageable);
    boolean existsByIdEventoAndIdParticipante(Integer idEvento, Integer idParticipante);

    // ✅ novo: listar só as inscrições do participante
    Page<ParticipanteEvento> findByIdParticipante(Integer idParticipante, Pageable pageable);
}
