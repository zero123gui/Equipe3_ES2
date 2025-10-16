// src/main/java/br/com/eventos/api/web/EventoController.java
package br.com.eventos.api.web;

import br.com.eventos.api.domain.Evento;
import br.com.eventos.api.dto.EventCreateDto;
import br.com.eventos.api.dto.EventUpdateDto;
import br.com.eventos.api.service.EventoService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/events")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    // criar (admin)
    @PostMapping
    public ResponseEntity<Evento> create(@RequestBody EventCreateDto dto) {
        var saved = service.create(dto);
        return ResponseEntity.created(URI.create("/events/" + saved.getId())).body(saved);
    }

    // listar (pode ser público, conforme sua SecurityConfig)
    @GetMapping
    public ResponseEntity<Page<Evento>> list(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.list(page, size));
    }

    // buscar por id (pode ser público)
    @GetMapping("/{id}")
    public ResponseEntity<Evento> get(@PathVariable Integer id) {
        return ResponseEntity.ok(service.get(id));
    }

    // atualizar (admin)
    @PutMapping("/{id}")
    public ResponseEntity<Evento> update(@PathVariable Integer id, @RequestBody EventUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // remover (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


