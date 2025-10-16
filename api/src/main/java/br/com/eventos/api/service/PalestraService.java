package br.com.eventos.api.service;

import br.com.eventos.api.domain.Palestra;
import br.com.eventos.api.dto.TalkCreateDto;
import br.com.eventos.api.dto.TalkUpdateDto;
import br.com.eventos.api.dto.TalkResponseDto;
import br.com.eventos.api.repo.PalestraRepository;
import br.com.eventos.api.repo.EventoRepository;
import br.com.eventos.api.security.SecurityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PalestraService {

    private final PalestraRepository repo;
    private final EventoRepository eventoRepo;
    private final SecurityService security;

    public PalestraService(PalestraRepository repo, EventoRepository eventoRepo, SecurityService security) {
        this.repo = repo;
        this.eventoRepo = eventoRepo;
        this.security = security;
    }

    /* helpers */
    private void requireAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var email = (auth == null) ? null : (String) auth.getPrincipal();
        if (email == null || !security.isAdmin(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas admin");
        }
    }

    private void validateBusiness(Palestra p) {
        if (!eventoRepo.existsById(p.getIdEvento()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento inexistente");

        if (p.getDtTermino().isBefore(p.getDtInicio()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dtTermino < dtInicio");

        if (!p.getHoraTermino().isAfter(p.getHoraInicio()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "horaTermino <= horaInicio");

        if (p.getQtdVagas() == null || p.getQtdVagas() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "qtdVagas inválido");
    }

    private TalkResponseDto toDto(Palestra p) {
        return new TalkResponseDto(
                p.getId(), p.getNomePalestra(), p.getLocal(), p.getDescricao(),
                p.getQtdVagas(), p.getDtInicio(), p.getDtTermino(),
                p.getHoraInicio(), p.getHoraTermino(), p.getIdEvento()
        );
    }

    /* use-cases */
    public TalkResponseDto create(TalkCreateDto dto) {
        requireAdmin();
        var p = new Palestra();
        p.setNomePalestra(dto.nomePalestra());
        p.setLocal(dto.local());
        p.setDescricao(dto.descricao());
        p.setQtdVagas(dto.qtdVagas());
        p.setDtInicio(dto.dtInicio());
        p.setDtTermino(dto.dtTermino());
        p.setHoraInicio(dto.horaInicio());
        p.setHoraTermino(dto.horaTermino());
        p.setIdEvento(dto.idEvento());

        validateBusiness(p);
        p = repo.save(p);
        return toDto(p);
    }

    public Page<TalkResponseDto> list(Pageable pg) {
        return repo.findAll(pg).map(this::toDto);
    }

    public TalkResponseDto get(Integer id) {
        var p = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Palestra não encontrada"));
        return toDto(p);
    }

    public TalkResponseDto update(Integer id, TalkUpdateDto dto) {
        requireAdmin();
        var p = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Palestra não encontrada"));

        p.setNomePalestra(dto.nomePalestra());
        p.setLocal(dto.local());
        p.setDescricao(dto.descricao());
        p.setQtdVagas(dto.qtdVagas());
        p.setDtInicio(dto.dtInicio());
        p.setDtTermino(dto.dtTermino());
        p.setHoraInicio(dto.horaInicio());
        p.setHoraTermino(dto.horaTermino());
        p.setIdEvento(dto.idEvento());

        validateBusiness(p);
        p = repo.save(p);
        return toDto(p);
    }

    public void delete(Integer id) {
        requireAdmin();
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Palestra não encontrada");
        }
        repo.deleteById(id);
    }
}
