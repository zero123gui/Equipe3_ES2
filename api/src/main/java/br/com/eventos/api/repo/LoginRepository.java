package br.com.eventos.api.repo;

import br.com.eventos.api.domain.Login;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Integer> {
    Optional<Login> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByParticipanteId(Integer participanteId);
}
