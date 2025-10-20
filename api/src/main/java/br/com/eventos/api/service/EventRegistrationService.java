package br.com.eventos.api.service;

import br.com.eventos.api.domain.ParticipanteEvento;
import br.com.eventos.api.domain.Login;
import br.com.eventos.api.dto.EventRegistrationCreateDto;
import br.com.eventos.api.dto.EventRegistrationResponseDto;
import br.com.eventos.api.repo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;
import br.com.eventos.api.security.SecurityService;

@Service
public class EventRegistrationService {

    private final ParticipanteEventoRepository regRepo;
    private final EventoRepository eventoRepo;
    private final TipoInscricaoRepository tipoInscRepo;
    private final LoginRepository loginRepo;
    private final SecurityService securityService; // precisa existir no projeto (isAdmin)

    public EventRegistrationService(ParticipanteEventoRepository regRepo,
                                    EventoRepository eventoRepo,
                                    TipoInscricaoRepository tipoInscRepo,
                                    LoginRepository loginRepo,
                                    SecurityService securityService) {
        this.regRepo = regRepo;
        this.eventoRepo = eventoRepo;
        this.tipoInscRepo = tipoInscRepo;
        this.loginRepo = loginRepo;
        this.securityService = securityService;
    }

    private String currentEmail() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sem autenticação");
        return (String) auth.getPrincipal();
    }

    private Integer currentParticipanteId() {
        String email = currentEmail();
        return loginRepo.findByEmail(email)
                .map(Login::getParticipanteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login não encontrado"));
    }

    public EventRegistrationResponseDto inscrever(EventRegistrationCreateDto dto) {
        Integer idEvento = dto.idEvento();
        Integer idTipoInscricao = dto.idTipoInscricao();

        if (!eventoRepo.existsById(idEvento))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado");
        if (!tipoInscRepo.existsById(idTipoInscricao))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de inscrição inválido");

        Integer pid = currentParticipanteId();

        if (regRepo.existsByIdEventoAndIdParticipante(idEvento, pid))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Inscrição já existe para este evento");

        var pe = new ParticipanteEvento();
        pe.setIdEvento(idEvento);
        pe.setIdTipoInscricao(idTipoInscricao);
        pe.setIdParticipante(pid);

        pe = regRepo.save(pe);

        return new EventRegistrationResponseDto(
                pe.getId(), pe.getIdEvento(), pe.getIdTipoInscricao(), pe.getIdParticipante()
        );
    }

    public Page<EventRegistrationResponseDto> listarPorEvento(Integer eventId, Pageable pageable) {
        if (!eventoRepo.existsById(eventId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado");

        // admin-only: checa aqui (ou deixe pro controller)
        String email = currentEmail();
        if (!securityService.isAdmin(email))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas admin");

        return regRepo.findByIdEvento(eventId, pageable)
                .map(pe -> new EventRegistrationResponseDto(
                        pe.getId(), pe.getIdEvento(), pe.getIdTipoInscricao(), pe.getIdParticipante()
                ));
    }

    public Page<EventRegistrationResponseDto> listarMinhasInscricoes(Pageable pageable) {
        Integer pid = currentParticipanteId();
        return regRepo.findByIdParticipante(pid, pageable)
                .map(pe -> new EventRegistrationResponseDto(
                        pe.getId(), pe.getIdEvento(), pe.getIdTipoInscricao(), pe.getIdParticipante()
                ));
    }


    public void cancelar(Integer regId) {
        String email = currentEmail();
        Integer pid = currentParticipanteId();
        boolean admin = securityService.isAdmin(email);

        var pe = regRepo.findById(regId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscrição não encontrada"));

        if (!admin && !pe.getIdParticipante().equals(pid))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode cancelar a sua inscrição");

        regRepo.deleteById(regId);
    }
}
