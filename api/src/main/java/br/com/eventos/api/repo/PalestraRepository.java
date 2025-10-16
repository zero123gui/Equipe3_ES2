package br.com.eventos.api.repo;

import br.com.eventos.api.domain.Palestra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PalestraRepository extends JpaRepository<Palestra, Integer> { }
