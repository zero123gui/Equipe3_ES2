package br.com.eventos.api.web;

import br.com.eventos.api.dto.EventRegistrationCreateDto;
import br.com.eventos.api.dto.EventRegistrationResponseDto;
import br.com.eventos.api.service.EventRegistrationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event-registrations")
public class EventRegistrationController {

    private final EventRegistrationService service;

    public EventRegistrationController(EventRegistrationService service) {
        this.service = service;
    }

    // USER: cria inscrição do usuário logado
    @PostMapping
    public ResponseEntity<EventRegistrationResponseDto> create(@RequestBody EventRegistrationCreateDto dto) {
        var out = service.inscrever(dto);
        return ResponseEntity.ok(out);
    }

    // ADMIN: lista inscrições de um evento
    @GetMapping
    public ResponseEntity<Page<EventRegistrationResponseDto>> listByEvent(
            @RequestParam("eventId") Integer eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var out = service.listarPorEvento(eventId, PageRequest.of(page, size));
        return ResponseEntity.ok(out);
    }

    // USER: minhas inscrições
    @GetMapping("/my")
    public ResponseEntity<Page<EventRegistrationResponseDto>> myRegs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var out = service.listarMinhasInscricoes(PageRequest.of(page, size));
        return ResponseEntity.ok(out);
    }

    // USER ou ADMIN: cancelar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Integer id) {
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
