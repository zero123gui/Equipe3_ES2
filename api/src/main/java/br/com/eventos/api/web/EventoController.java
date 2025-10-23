// src/main/java/br/com/eventos/api/web/EventoController.java
package br.com.eventos.api.web;

import br.com.eventos.api.domain.Evento;
import br.com.eventos.api.dto.EventoFullCreateDto;
import br.com.eventos.api.dto.EventoFullCreateResponse;
import br.com.eventos.api.service.EventoFullService;
import br.com.eventos.api.service.EventoService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/events")
public class EventoController {

    private final EventoService service;
    private final EventoFullService fullService;

    public EventoController(EventoService service, EventoFullService fullService) {
        this.service = service;
        this.fullService = fullService;
    }

    // criar (ADMIN) – agora sempre com endereço completo no body
    @PostMapping(value = "/full", consumes = "application/json", produces = "application/json")
    public ResponseEntity<EventoFullCreateResponse> createFull(@RequestBody EventoFullCreateDto dto) {
        var resp = fullService.criar(dto);
        return ResponseEntity.status(201).location(URI.create("/events/" + resp.idEvento())).body(resp);
    }

    // atualizar (ADMIN) – idem, recebendo o body completo com endereço
    @PutMapping("/{id}")
    public ResponseEntity<EventoFullCreateResponse> update(@PathVariable Integer id,
                                                           @RequestBody EventoFullCreateDto dto) {
        var resp = fullService.atualizar(id, dto);
        return ResponseEntity.ok(resp);
    }

    // listar (público/depende da security)
    @GetMapping
    public ResponseEntity<Page<Evento>> list(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.list(page, size));
    }

    // buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<Evento> get(@PathVariable Integer id) {
        return ResponseEntity.ok(service.get(id));
    }

    // remover (ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
