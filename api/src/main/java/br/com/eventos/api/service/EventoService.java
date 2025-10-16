// src/main/java/br/com/eventos/api/service/EventoService.java
package br.com.eventos.api.service;

import br.com.eventos.api.domain.Evento;
import br.com.eventos.api.dto.EventCreateDto;
import br.com.eventos.api.dto.EventUpdateDto;
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

    // util: pega e-mail logado do SecurityContext (setado no JwtAuthFilter)
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

    public Evento create(EventCreateDto dto) {
        requireAdmin();
        Evento e = new Evento();
        e.setNomeEvento(dto.nomeEvento());
        e.setDtInicio(dto.dtInicio());
        e.setDtTermino(dto.dtTermino());
        e.setLocal(dto.local());
        e.setDescricao(dto.descricao());
        e.setUrlSite(dto.urlSite());
        return repo.save(e);
    }

    public Page<Evento> list(int page, int size) {
        return repo.findAll(PageRequest.of(page, size));
    }

    public Evento get(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
    }

    public Evento update(Integer id, EventUpdateDto dto) {
        requireAdmin();
        Evento e = get(id);
        e.setNomeEvento(dto.nomeEvento());
        e.setDtInicio(dto.dtInicio());
        e.setDtTermino(dto.dtTermino());
        e.setLocal(dto.local());
        e.setDescricao(dto.descricao());
        e.setUrlSite(dto.urlSite());
        return repo.save(e);
    }

    public void delete(Integer id) {
        requireAdmin();
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado");
        }
        repo.deleteById(id);
    }
}
