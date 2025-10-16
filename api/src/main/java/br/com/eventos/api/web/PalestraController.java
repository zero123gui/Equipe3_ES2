package br.com.eventos.api.web;

import br.com.eventos.api.dto.TalkCreateDto;
import br.com.eventos.api.dto.TalkResponseDto;
import br.com.eventos.api.dto.TalkUpdateDto;
import br.com.eventos.api.service.PalestraService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/talks")
public class PalestraController {

    private final PalestraService service;

    public PalestraController(PalestraService service) {
        this.service = service;
    }

    // POST /talks (ADMIN)
    @PostMapping
    public TalkResponseDto create(@RequestBody TalkCreateDto dto) {
        return service.create(dto);
    }

    // GET /talks?page=&size=
    @GetMapping
    public Page<TalkResponseDto> list(Pageable pageable) {
        return service.list(pageable);
    }

    // GET /talks/{id}
    @GetMapping("/{id}")
    public TalkResponseDto get(@PathVariable Integer id) {
        return service.get(id);
    }

    // PUT /talks/{id} (ADMIN)
    @PutMapping("/{id}")
    public TalkResponseDto update(@PathVariable Integer id, @RequestBody TalkUpdateDto dto) {
        return service.update(id, dto);
    }

    // DELETE /talks/{id} (ADMIN)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
