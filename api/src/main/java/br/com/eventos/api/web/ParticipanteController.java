package br.com.eventos.api.web;

import br.com.eventos.api.domain.Participante;
import br.com.eventos.api.dto.ParticipanteCreateDto;
import br.com.eventos.api.dto.ParticipanteUpdateDto;
import br.com.eventos.api.service.ParticipanteService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/participants")
public class ParticipanteController {

    private final ParticipanteService service;

    public ParticipanteController(ParticipanteService service) {
        this.service = service;
    }

    // RF210 - Inserção
    @PostMapping
    public ResponseEntity<Participante> create(@RequestBody ParticipanteCreateDto dto) {
        var saved = service.create(dto);
        var uri = URI.create("/participants/" + saved.getId());
        return ResponseEntity.created(uri).body(saved);
    }

    // RF230 - Consulta (lista paginada)
    @GetMapping
    public ResponseEntity<Page<Participante>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.list(page, size));
    }

    // RF230 - Consulta por id
    @GetMapping("/{id}")
    public ResponseEntity<Participante> get(@PathVariable Integer id) {
        return ResponseEntity.ok(service.get(id));
    }

    // RF240 - Alteração
    @PutMapping("/{id}")
    public ResponseEntity<Participante> update(@PathVariable Integer id,
                                               @RequestBody ParticipanteUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // RF220 - Remoção
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
