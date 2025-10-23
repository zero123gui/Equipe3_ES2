// src/main/java/br/com/eventos/api/service/EventoService.java
package br.com.eventos.api.service;

import br.com.eventos.api.domain.Evento;
import br.com.eventos.api.repo.EventoRepository;
import br.com.eventos.api.security.SecurityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EventoService {

    private final EventoRepository repo;
    private final SecurityService security;

    public EventoService(EventoRepository repo, SecurityService security) {
        this.repo = repo;
        this.security = security;
    }

    private String currentEmail() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth == null) ? null : (String) auth.getPrincipal();
    }

    private void requireAdmin() {
        String email = currentEmail();
        if (email == null || !security.isAdmin(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas admin");
        }
    }

    public Page<Evento> list(int page, int size) {
        return repo.findAll(PageRequest.of(page, size));
    }

    public Evento get(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
    }

    public void delete(Integer id) {
        requireAdmin();
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado");
        }
        repo.deleteById(id);
    }
}
