package br.com.eventos.api.service;

import br.com.eventos.api.domain.Participante;
import br.com.eventos.api.dto.ParticipanteCreateDto;
import br.com.eventos.api.dto.ParticipanteUpdateDto;
import br.com.eventos.api.repo.ParticipanteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class ParticipanteService {

    private final ParticipanteRepository repo;

    public ParticipanteService(ParticipanteRepository repo) {
        this.repo = repo;
    }

    public Participante create(ParticipanteCreateDto dto) {
        Participante p = new Participante();
        p.setNome(dto.nome());
        p.setIdTipoParticipante(dto.idTipoParticipante());
        p.setIdEndereco(dto.idEndereco());
        p.setComplementoEndereco(dto.complementoEndereco());
        p.setNroEndereco(dto.nroEndereco());
        return repo.save(p);
    }

    public Page<Participante> list(int page, int size) {
        return repo.findAll(PageRequest.of(page, size));
    }

    public Participante get(Integer id) {
        return repo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participante " + id + " não encontrado")
        );
    }

    public Participante update(Integer id, ParticipanteUpdateDto dto) {
        Participante p = get(id);
        p.setNome(dto.nome());
        p.setIdTipoParticipante(dto.idTipoParticipante());
        p.setIdEndereco(dto.idEndereco());
        p.setComplementoEndereco(dto.complementoEndereco());
        p.setNroEndereco(dto.nroEndereco());
        return repo.save(p);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}
